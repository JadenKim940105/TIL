# 알고리즘&자료구조#1 Selection Sort

## 선택정렬
```text
1. 주어진 리스트 중 최솟값을 찾는다.
2. 최솟값을 맨 앞의 값과 교체한다.
3. 맨 처음 위치를 제외하고, 반복. 

최악 시간복잡도	On^2 비교, On 교환
최선 시간복잡도	On^2 비교, On 교환
평균 시간복잡도	On^2 비교, On 교환
```

### 코드구현
```java
public int[] selectionSort(int n, int[] arr) {
    for(int i = 0; i < n-1; i++){
        int idx = i; // 맨 앞의 값 
        for(int j = i+1; j < n; j++){
            if(arr[j] < arr[idx]){ // 이후의 값중에서 더 작은 값을 찾는다. 
                idx = j;
            }
        }
        // 가장 작은 값을 맨 앞의 값과 교체 
        int tmp = arr[i];   
        arr[i] = arr[idx];
        arr[idx] = tmp;
    }
    return arr;
}
```



