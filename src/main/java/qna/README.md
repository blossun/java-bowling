# 1단계 - 질문 삭제하기 기능 리팩토링

## 기능 요구사항
- 단위 테스트 가능한 코드 분리
- 단위 테스트 구현 
- 기존 로직 정상 동작
    - QnaServiceTest 수행을 통해 정상동작 확인


## 질문 삭제하기 기능 요구사항
- 질문 데이터를 완전히 삭제하는 것이 아니라 데이터의 상태를 삭제 상태(deleted - boolean type)로 변경
- 로그인 사용자와 질문한 사람이 같은 경우 삭제 가능
- 답변이 없는 경우 삭제 가능
- 질문자와 답변글의 모든 답변자 같은 경우 삭제 가 가능
- 질문을 삭제할 때 답변 또한 삭제해야 하며, 답변의 삭제 또한 삭제 상태(deleted)를 변경
- 질문자와 답변자가 다른 경우 답변을 삭제할 수 없음
- 질문과 답변 삭제 이력에 대한 정보를 DeleteHistory를 활용해 남김
