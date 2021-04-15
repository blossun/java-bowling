# 볼링 게임 점수판
## 진행 방법
* 볼링 게임 점수판 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정

* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

## 1단계 - 질문 삭제하기 기능 리팩토링

- 질문 삭제하기 요구사항
	- 질문 데이터를 완전히 삭제하는 것이 아니라 데이터의 상태를 삭제상태(deleted - boolean type)로 변경한다.
	- 로그인 사용자와 질문한 사람이 같은 경우 삭제 가능하다.
	- 답변이 없는 경우 삭제가 가능하다.
	- 질문자와 답변 글의 모든 답변자 같은 경우 삭제가 가능하다.
	- 질문을 삭제할 때 답변 또한 삭제해야 하며, 답변의 삭제 또한 삭제 상태(deleted)를 변경한다.
	- 질문자와 답변자가 다른경우 답변을 삭제할 수 없다.
	- 질문과 답변 삭제 이력에 대한 정보를 `DeleteHistory`를 활용해 남긴다.

- 프로그래밍 요구사항
	- qna.service.QnaService의 deleteQuestion()는 앞의 질문 삭제 기능을 구현한 코드이다.
	- 이 메소드는 단위 테스트하기 어려운 코드와 단위 테스트 가능한 코드가 섞여 있다.
	- 단위 테스트하기 어려운 코드와 단위 테스트 가능한 코드를 분리해 단위 테스트 가능한 코드 에 대해 단위 테스트를 구현한다.

```java
public class QnAService {
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = findQuestionById(questionId);
        if (!question.isOwner(loginUser)) {
            throw new CannotDeleteException("질문을 삭제할 권한이 없습니다.");
        }

        List<Answer> answers = question.getAnswers();
        for (Answer answer : answers) {
            if (!answer.isOwner(loginUser)) {
                throw new CannotDeleteException("다른 사람이 쓴 답변이 있어 삭제할 수 없습니다.");
            }
        }

        List<DeleteHistory> deleteHistories = new ArrayList<>();
        question.setDeleted(true);
        deleteHistories.add(new DeleteHistory(ContentType.QUESTION, questionId, question.getWriter(), LocalDateTime.now()));
        for (Answer answer : answers) {
            answer.setDeleted(true);
            deleteHistories.add(new DeleteHistory(ContentType.ANSWER, answer.getId(), answer.getWriter(), LocalDateTime.now()));
        }
        deleteHistoryService.saveAll(deleteHistories);
    }
}
```

- 힌트1
	- 객체의 상태 데이터를 꺼내지(get)말고 메시지를 보낸다.
	- 규칙 8: 일급 콜렉션을 쓴다.
	- Question의 List를 일급 콜렉션으로 구현해 본다.
	- 규칙 7: 3개 이상의 인스턴스 변수를 가진 클래스를 쓰지 않는다.
	- 인스턴스 변수의 수를 줄이기 위해 도전한다.

- 힌트2
	- 테스트하기 쉬운 부분과 테스트하기 어려운 부분을 분리해 테스트 가능한 부분만 단위테스트한다.


> TDD 순서(잘 안지켜짐)

- 엔티티 내에 List<> 타입의 필드를 일급 컬렉션으로 분리
	- List<Answer> -> Answers
	
- Question 과 해당 Question에 대한 Answers가 동일한 작성자인 경우 삭제 가능
    - 권한 여부(checkAuthorization)

- 질문 삭제 시 답변 또한 삭제, 답변의 삭제 또한 삭제 상태 변경
	- setter 삭제 후, 삭제 필드에 대한 수정을 관련 로직에 포함

## QnA 피드백 강의

- 비즈니스 로직이 Service Layer에 집중 되어 있다.
- 비즈니스 로직은 Mock을 통해 단위 테스트 할 수는 있다.
	- 하지만 Mock에 대한 학습 비용, 테스트 코드 작성에 대한 부담이 커진다.

- 비즈니스 로직을 OOP로 리펙토링하기 위해서는 단위 테스트 코드가 불가피하게 필요하다.
	- 해당 테스트 코드를 OOP로 리펙토링하면서 테스트가 깨지지 않도록 한다.
	- 비즈니스 코드는 트랜잭션, 권한, 메시지 보내는 코드에 대한 로직만 갖고 있도록 한다.

- Mock은 최소화하고 JUnit 테스트를 통해 개발한다.
- 서비스를 최소화하고, 도메인으로 구성하기

- 도메인 설계 -> 구현의 반복
	- 요구사항 분석 후, 도메인 설계
	

- TDD로 구현하면서 인터페이스, 도메인 설계
	- 반복을 통한 설계 개선
