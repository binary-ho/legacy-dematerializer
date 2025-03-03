#include <stdio.h>

/* 여러 줄에 걸친 함수 시그니처 예제 */
int 
computeAndPrint(
    int x, 
    int y
) {
    // 두 수를 곱한 결과에 다른 함수를 적용 (중첩된 호출 관계 확인용)
    int result = multiplyAndAdd(x, y);  // multiplyAndAdd 함수 호출
    printResult(result);               // printResult 함수 호출
    // addNumbers(x, y); 함수는 위에서 호출되었음 (주석 내 호출은 무시되어야 함)
    return result;
}

/* 두 함수를 순차로 호출하여 결과를 계산 */
int multiplyAndAdd(int a, int b) {
    int product = multiplyNumbers(a, b);   // multiplyNumbers 함수 호출
    return addNumbers(product, a);         // addNumbers 함수 호출
}

/* 두 정수를 더하는 함수 */
int addNumbers(int a, int b) {
    return a + b;
}

/* 두 정수를 곱하는 함수 */
int multiplyNumbers(int a, int b) {
    return a * b;
}

/* 계산 결과를 출력하는 함수 */
void printResult(int value) {
    printf("Result is %d\n", value);
}

int main() {
    int total = computeAndPrint(5, 3);  // computeAndPrint 함수 호출
    return 0;
}