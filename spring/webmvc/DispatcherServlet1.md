# Spring web MVC#3 DispatcherServlet

## 디스패처서블릿 동작원리
디스패처서블릿의 동작흐름을 이해하기 위해 스프링이 제공하는 디스패처 서블릿을 아주 간단하게 살펴보도록 하겠다.

#### doService()
```java
	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logRequest(request);

		Map<String, Object> attributesSnapshot = null;
		if (WebUtils.isIncludeRequest(request)) {
			attributesSnapshot = new HashMap<>();
			Enumeration<?> attrNames = request.getAttributeNames();
			while (attrNames.hasMoreElements()) {
				String attrName = (String) attrNames.nextElement();
				if (this.cleanupAfterInclude || attrName.startsWith(DEFAULT_STRATEGIES_PREFIX)) {
					attributesSnapshot.put(attrName, request.getAttribute(attrName));
				}
			}
		}
		
		request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
		request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
		request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
		request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());

		if (this.flashMapManager != null) {
			FlashMap inputFlashMap = this.flashMapManager.retrieveAndUpdate(request, response);
			if (inputFlashMap != null) {
				request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, Collections.unmodifiableMap(inputFlashMap));
			}
			request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
			request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, this.flashMapManager);
		}

		RequestPath previousRequestPath = null;
		if (this.parseRequestPath) {
			previousRequestPath = (RequestPath) request.getAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE);
			ServletRequestPathUtils.parseAndCache(request);
		}
		
		/*
                위의 코드를 대충 보면 로케일정보, 테마정보를 분석하는 코드들이 보이고 요청이 flashMap 인지 이전요청경로는 무엇인지 등등, 
                요청정보를 분석하는 과정이다. 
		 */

		try {
			doDispatch(request, response); // doDispatch 를 타고들어가보자 
		}
		finally {
			if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
				if (attributesSnapshot != null) {
					restoreAttributesAfterInclude(request, attributesSnapshot);
				}
			}
			ServletRequestPathUtils.setParsedRequestPath(previousRequestPath, request);
		}
	}
```


#### doDispatch()
````java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpServletRequest processedRequest = request;
		HandlerExecutionChain mappedHandler = null;
		boolean multipartRequestParsed = false;

		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

		try {
			ModelAndView mv = null;
			Exception dispatchException = null;

			try {
				processedRequest = checkMultipart(request);
				multipartRequestParsed = (processedRequest != request);
				
				mappedHandler = getHandler(processedRequest);  
        /*
                요청을 처리할 handler 를 찾아온다. 내부를 살펴보면 handler 를 찾을 수 있는 handlerMapping 을 찾고, 
                handlerMapping 을 통해 handler 를 찾는 과정이 있다.
                * 참고: annotation 기반의 handler 는 RequestMappingHandlerMapping 을 통해 찾을 수 있다.
         */
				if (mappedHandler == null) {
					noHandlerFound(processedRequest, response);
					return;
				}
				
            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
        /*
                handler 를 실행할 수 있는 handlerAdapter 를 찾아온다.
         */
				String method = request.getMethod();
				boolean isGet = "GET".equals(method);
				if (isGet || "HEAD".equals(method)) {
					long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
					if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
						return;
					}
				}
				

				if (!mappedHandler.applyPreHandle(processedRequest, response)) {
					return;
				}
				
				mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
        /*
                ha (handler adapter) 를 사용해 요청을 처리한다.  
         */
				if (asyncManager.isConcurrentHandlingStarted()) {
					return;
				}

				applyDefaultViewName(processedRequest, mv);
				mappedHandler.applyPostHandle(processedRequest, response, mv);
			}
			catch (Exception ex) {
				dispatchException = ex;
			}
			catch (Throwable err) {
				dispatchException = new NestedServletException("Handler dispatch failed", err);
			}
			processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
		}
		catch (Exception ex) {
			triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
		}
		catch (Throwable err) {
			triggerAfterCompletion(processedRequest, response, mappedHandler,
					new NestedServletException("Handler processing failed", err));
		}
		finally {
			if (asyncManager.isConcurrentHandlingStarted()) {
				if (mappedHandler != null) {
					mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
				}
			}
			else {
				if (multipartRequestParsed) {
					cleanupMultipart(processedRequest);
				}
			}
		}
	}
````

>1. 요청이 들어온다   
> 2. 서블릿 컨테이너의 쓰레드 풀에서 쓰레드를 할당받는다. 
> 3. 쓰레드 내에서 DispatcherServlet 이 요청을 받고 doService() 실행 
> 4. 요청을 분석하고, doDispatch() 실행  
> - doDispatch[1] 요청을 처리할 handler 를 찾아온다   
>    - handler 찾는 과정  
        - 요청을 처리할 handler 를 찾을 수 있는 handlerMapping 을 찾는다  
        - handlerMapping 을 통해 handler 를 찾는다.
> - doDispatch()[2] handlerAdapter 를 통해 실행
> 5. 처리된 결과를 응답에 실어 보낸다. 

