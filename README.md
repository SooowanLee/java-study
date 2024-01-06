## 자바에 대해 공부한 내용
<details>
<summary>안전한 Java 클래스 설계</summary>

## src/main/java/com/study/java/user

</br>안전한 클래스 설계를 설명하기 전에 Java에서 클래스에 대해 간략하게 설명하자면

> 클래스는 코드를 작성하기 위한 초기 단계, 즉 객체 설계도의 역할을 합니다.
객체 == 클래스라고 혼동해서는 안됩니다. 객체를 표현하기 위한 수단이 클래스일 뿐입니다.

## 1. 생성자를 통한 변수 초기화

```java
public class User {

    private final String name;
    private final Team team;

    public User(String name, Team team) {
        this.name = name;
        this.team = team;
    }
}
```

- 생성자를 통해 변수를 초기화 하면 예상하지 못한 값이 들어올 수 있다.
- 매개변수에 전혀 관계가 없는 값을 넣거나 null을 넣어도 할당이 가능하기 때문이다.

```java
//잘못된 값 초기화
User user = new User("-1", null);
```

- 이상한 값을 넣고 초기화 한 결과

![unusual.png](src%2Fmain%2Fresources%2Fimage%2Funusual.png)

- 잘못된 값이 초기화되지 않도록 유효성 검사를 객체가 생성될 때 진행한다.
  - 이름(name) : 숫자가 들어올 수 없다.
  - 팀(Team) : null이 들어오면 안된다.

```java
public class User {

    private final String name;
    private final Team team;

    public User(String name, Team team) {
        validate(name, team);
        this.name = name;
        this.team = team;
    }

    private void validate(String name, Team team) {
        if (isNumeric(name)) {
            throw new IllegalArgumentException("이름은 숫자일 수 없습니다.");
        }

        if (Objects.isNull(team)) {
            throw new NullPointerException("팀을 지정해주세요.");
        }
    }

    private boolean isNumeric(String name) {
        return Pattern.matches("^(0|[-]?[1-9]\\d*)$", name);
    }
}
```

![validate_result.png](src%2Fmain%2Fresources%2Fimage%2Fvalidate_result.png)

위 코드처럼 생성자에서 유효성 검사를 하면 애초에 잘못된 값이 들어올 수 없다.

**항상 안전하고 정상적인 객체를 보장할 수 있다.**

## 2. 불변한 변수를 통해 예상치 못한 변경을 예방하기

변경이 가능한 변수가 가지는 위험은 크게 3가지가 있다.

- 버그 발생 가능성 증가 : 가변 변수를 다룰 때는 값을 주의 깊게 추적하지 않으면 어디서 어떻게 값이 바뀌는지 알 수 없기 때문에 예상하지 못한 버그를 발생 시킬 수 있다.
- 코드 이해의 어려움 : 값의 변경을 추적하기 어려워 코드의 복잡성을 증가시키고, 이는 유지보수의 어려움도 가져온다.
- 동시성 문제 : 여러 스레드에서 가변 변수에 동시에 접근하는 경우, 경쟁 조건과 데드락과 같은 동시성 문제가 발생할 수 있다. 적절한 동기화가 이루어지지 않으면 예측할 수 없는 결과가 발생할 수 있습니다.
- 위의 3가지 문제 이외에도 테스트의 어려움, 메모리사용 등 여러 문제가 발생한다.

위의 문제들을 해결하기 위해 불변으로 변수를 만든다면 장점을 얻을 수 있다.

- 변경이 없기 때문에 혼란을 줄일 수 있다.
- 안정적인 동작이 가능하고, 결과를 예측할 수 있다.
- 코드를 이해하기 쉬워지고, 유지보수가 편리해진다.

```java
public class User {

    private final String name;
    private final Team team;

		public User(String name, Team team) {
        validate(name, team);
        this.name = name;
        this.team = team;
	  }
}
```

## 3. 변경을 해야 한다면, 새로운 인스턴스 생성하기

- 객체의 상태를 변경하는 대신, 새로운 객체를 만들어서 기존 객체의 상태를 변경하지 않고 새로운 상태를 나타내는 것을 의미한다.
- 코드가 예측 가능하고 안전해진다.

```java
public User changeTeam(Team newTeam) {
        validate(this.name, newTeam);
        return new User(this.name, newTeam);
    }
```

## 4. 메서드 매개변수와 지역변수 불변으로 만들기

```java
private void changeName(String name) {
        name = "test1";
        //...
        name = "test2";
    }
```

- 매개변수를 가변적으로 만들게 되면 로직을 수행하면서 변경이 일어날 수 있다.
- 이는 위에서 계속 말하는 것과 마찬가지로 예측이 불가능하고 계속 해서 추적을 해야 하기 때문에 유지보수에 어려움이 있다.

```java
private void changeName(final String name) {
        name = "test1"; //컴파일 오류
        //...
        name = "test2"; //컴파일 오류
    }
```

- final 키워드를 사용해 매개변수를 불변하게 만든다면 컴파일 에러가 발생하기 때문에 최소한의 방어가 가능하다.
- **참고로 에러는 없는 것이 좋겠지만 그건 불가능 하기 때문에 최대한 컴파일 오류로 개발 시에 오류가 나는게 좋다고 생각한다.**

## 5. 메서드에 전달하려는 값의 타입을 명확히 하기

- 이 방법은 **`Team`** 객체의 불변성과도 관련이 있습니다. **`User`** 클래스가 불변이라면, **`changeTeam`** 메서드를 호출한 후에도 기존 사용자 객체는 그대로 유지되고, 새로운 사용자 객체가 반환됩니다.

```java
public User changeTeam(final Team newTeam) {
        validate(this.name, newTeam);
        return new User(this.name, newTeam);
    }
```

```java
public class Main {
    public static void main(String[] args) {
        // 기존 사용자 생성
        Team originalTeam = new Team("OriginalTeam");
        User user = new User("John", originalTeam);

        // 새로운 팀 생성
        Team newTeam = new Team("NewTeam");

        // changeTeam 메서드를 사용하여 팀 변경
        user = user.changeTeam(newTeam);

        // 변경된 사용자 정보 출력
        user.printUser();
    }
}
```

## 결론

위의 안전한 클래스를 작성하는 방법을 통해 얻을 수 있는 이점은

- 예측이 가능한 코드, 가독성 증가
- 생성자에서 유효성 검사를 하기 때문에 안전하게 객체를 생성, 믿고 사용할 수 있는 객체를 만들 수 있다.
- 클래스의 높은 응집도로 인해 유지보수 비용이 감소하는 효과가 있다.
- 동일한 입력값에 대해 항상 동일한 결과를 반환하므로 테스트 케이스를 작성하거나 디버깅하기 용이해진다.

### 참고

https://redcoder.tistory.com/182

https://zzang9ha.tistory.com/454

[https://velog.io/@wellsy1012/객체지향-클린코드-Java](https://velog.io/@wellsy1012/%EA%B0%9D%EC%B2%B4%EC%A7%80%ED%96%A5-%ED%81%B4%EB%A6%B0%EC%BD%94%EB%93%9C-Java)
</details>
