# 부록 02. JavaFX — 현대적 자바 GUI

[부록 01. Swing](부록-01-Swing.md)이 오래된 표준 GUI 툴킷이라면, **JavaFX**는 그 뒤를 잇는 현대적 GUI 프레임워크다. Swing보다 세련된 컨트롤, CSS 기반 스타일링, 속성 바인딩, 부드러운 애니메이션·미디어 재생을 기본 제공해 데스크톱 앱의 "요즘 방식"으로 자리 잡았다.

이 부록은 JavaFX의 구조(Application/Stage/Scene), 레이아웃, 이벤트, 속성·바인딩, 컨트롤, 메뉴·툴바, 다이얼로그, CSS, 스레드 처리, 애니메이션을 개념과 짧은 코드로 정리한다.

> ⚠️ **실행 환경 주의 (두 가지)**
> 1. **JavaFX는 JDK에 포함되어 있지 않다.** (JDK 11부터 분리됨) 별도의 **JavaFX SDK를 내려받아 모듈 경로에 추가**하거나, Maven/Gradle 의존성(`org.openjfx:javafx-controls` 등)으로 가져와야 한다. 아무 설정 없이 `javac`만 하면 `package javafx.* does not exist` 오류가 난다.
> 2. **GUI라서 헤드리스 컨테이너에서 실행 불가.** 이 프로젝트가 도는 Docker/CI에는 디스플레이가 없어 창이 뜨지 않는다. 아래 코드는 **JavaFX SDK를 설정한 본인의 로컬 데스크톱**에서 실행해야 한다. `./run.sh`나 컨테이너로는 동작하지 않는다.

> 📘 실행 예시(모듈 경로 방식):
> ```bash
> java --module-path /path/to/javafx-sdk/lib \
>      --add-modules javafx.controls,javafx.fxml \
>      HelloFX
> ```

---

## 1. JavaFX 개요와 프로젝트 구조

JavaFX 애플리케이션은 세 계층으로 이루어진다. 극장에 비유하면 이해가 쉽다.

- **`Application`** — 앱의 진입점. `javafx.application.Application`을 상속하고 `start(Stage)`를 재정의한다. 극장 건물 전체.
- **`Stage`** — 실제 창(윈도우). 무대.
- **`Scene`** — 창에 표시되는 화면 한 장. 무대 위에 올려진 장면. 컴포넌트들의 트리(**Scene Graph**)를 담는다.

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HelloFX extends Application {
    @Override
    public void start(Stage stage) {              // JavaFX가 UI 스레드에서 호출
        Label label = new Label("안녕하세요, JavaFX!");
        Scene scene = new Scene(label, 400, 300);  // 루트 노드 + 크기
        stage.setTitle("첫 JavaFX 창");
        stage.setScene(scene);
        stage.show();                              // 창 표시
    }
    public static void main(String[] args) {
        launch(args);                              // Application 시작
    }
}
```

`launch`가 JavaFX 런타임을 띄우고 `start`를 자동 호출한다. UI 트리를 이루는 각 요소(레이블·버튼·레이아웃)는 모두 **`Node`**를 상속한다.

---

## 2. 레이아웃과 컨테이너

JavaFX도 레이아웃 컨테이너가 자식 노드를 자동 배치한다. Swing 레이아웃과 대응되지만 이름이 다르다.

- **`VBox` / `HBox`** — 세로 / 가로 한 줄 배치.
- **`BorderPane`** — 상/하/좌/우/중앙 다섯 구역(Swing `BorderLayout`과 유사).
- **`GridPane`** — 행×열 격자. 각 노드를 `add(node, col, row)`로 배치.
- **`StackPane`** — 노드를 겹쳐 쌓기(가운데 정렬 기본).

```java
VBox root = new VBox(10);                  // 자식 간 간격 10px
root.getChildren().addAll(
    new Label("이름:"),
    new TextField(),
    new Button("확인")
);

GridPane grid = new GridPane();
grid.add(new Label("아이디"), 0, 0);       // (열, 행)
grid.add(new TextField(),     1, 0);
```

Swing이 `add(comp)`를 쓰는 것과 달리, JavaFX는 컨테이너의 **`getChildren().add(...)`** 로 자식을 넣는다.

---

## 3. 이벤트 처리

JavaFX 이벤트도 리스너(핸들러)를 등록하는 방식이다. 버튼 클릭은 `setOnAction`으로 처리하며, 인자로 `EventHandler`(함수형 인터페이스)를 받으므로 람다가 잘 어울린다.

```java
Button button = new Button("눌러보세요");
Label  label  = new Label("아직 안 눌림");

button.setOnAction(event -> label.setText("눌렸습니다!"));
```

마우스는 `setOnMouseClicked`, 키 입력은 `setOnKeyPressed` 등 `setOn~` 계열 메서드가 준비되어 있다. Swing의 `addActionListener`와 개념은 같다.

---

## 4. 속성(Property)과 바인딩(Binding)

JavaFX가 Swing보다 크게 앞서는 부분이 **속성·바인딩**이다. JavaFX 컨트롤의 값(텍스트, 너비, 선택 상태 등)은 단순 필드가 아니라 **`Property` 객체**로 노출된다(`textProperty()`, `widthProperty()` …). 이 속성끼리 **바인딩**하면 한쪽이 바뀔 때 다른 쪽이 자동으로 따라간다.

```java
TextField input  = new TextField();
Label     mirror = new Label();

// input의 텍스트가 바뀌면 mirror가 자동으로 같은 값으로 갱신됨
mirror.textProperty().bind(input.textProperty());

// 리스너로 변화를 감지할 수도 있다
input.textProperty().addListener((obs, oldVal, newVal) ->
    System.out.println("바뀜: " + newVal));
```

바인딩 덕분에 "값이 바뀔 때마다 화면을 수동으로 갱신"하는 반복 코드를 대폭 줄인다. 이는 오늘날 프론트엔드의 데이터 바인딩과 같은 발상이다.

---

## 5. 컨트롤(주요 컴포넌트)

JavaFX가 제공하는 대표 컨트롤. Swing 대응물과 이름이 살짝 다르다(대부분 `J` 접두어가 없다).

- **`Button`** — 클릭 버튼(`setOnAction`).
- **`Label`** — 텍스트 표시.
- **`TextField` / `TextArea`** — 한 줄 / 여러 줄 입력.
- **`CheckBox` / `RadioButton`** — 체크박스 / 라디오(`ToggleGroup`으로 묶음).
- **`ComboBox<T>`** — 드롭다운 선택.
- **`ListView<T>`** — 세로 목록.
- **`TableView<T>`** — 행/열 표. 각 열은 `TableColumn`으로 정의하고 속성에 바인딩.
- **`TreeView<T>`** — 계층 트리.

```java
ComboBox<String> combo = new ComboBox<>();
combo.getItems().addAll("사과", "바나나", "포도");

ListView<String> list = new ListView<>();
list.getItems().addAll("항목1", "항목2", "항목3");
```

---

## 6. 메뉴와 툴바

- **`MenuBar`** — 상단 메뉴 막대.
- **`Menu`** — "파일" 같은 메뉴.
- **`MenuItem`** — 메뉴 내 항목(`setOnAction`).
- **`ToolBar`** — 버튼을 늘어놓는 도구 막대.

```java
MenuBar menuBar = new MenuBar();
Menu    fileMenu = new Menu("파일");
MenuItem openItem = new MenuItem("열기");
openItem.setOnAction(e -> System.out.println("열기 클릭"));
fileMenu.getItems().add(openItem);
menuBar.getMenus().add(fileMenu);

ToolBar toolBar = new ToolBar(new Button("새로"), new Button("저장"));

BorderPane root = new BorderPane();
root.setTop(new VBox(menuBar, toolBar));   // 메뉴+툴바를 위쪽에
```

---

## 7. 다이얼로그

간단한 알림·확인·입력은 `Alert`와 `TextInputDialog`로 처리한다(Swing의 `JOptionPane` 대응).

```java
Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "정말 삭제할까요?");
alert.showAndWait().ifPresent(result -> {
    if (result == ButtonType.OK) { /* 삭제 진행 */ }
});

TextInputDialog dialog = new TextInputDialog();
dialog.setHeaderText("이름을 입력하세요");
dialog.showAndWait().ifPresent(name -> System.out.println("입력: " + name));
```

`showAndWait()`가 `Optional`을 돌려주므로 결과 유무를 안전하게 다룰 수 있다.

---

## 8. CSS 스타일

JavaFX는 웹처럼 **CSS로 UI 외형을 꾸민다.** 코드에서 색·폰트를 하드코딩하지 않고 스타일시트로 분리할 수 있어 유지보수가 쉽다.

```java
// 개별 노드에 인라인 스타일
button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

// 외부 CSS 파일 연결
scene.getStylesheets().add(getClass().getResource("app.css").toExternalForm());
```

```css
/* app.css — JavaFX 전용 속성은 -fx- 접두어 */
.button {
    -fx-font-size: 14px;
    -fx-background-radius: 8;
}
```

노드에 `setId("myBtn")`이나 `getStyleClass().add("primary")`를 주면 CSS 선택자(`#myBtn`, `.primary`)로 골라 스타일을 적용한다.

---

## 9. 스레드와 UI 변경 — Platform.runLater

Swing의 EDT처럼, JavaFX도 **"UI는 JavaFX Application Thread에서만 변경"**하는 규칙이 있다. 백그라운드 스레드에서 UI를 직접 건드리면 예외가 난다. 다른 스레드에서 UI를 바꿔야 하면 **`Platform.runLater`**로 작업을 UI 스레드에 넘긴다.

```java
new Thread(() -> {
    String result = 무거운작업();                  // 백그라운드 스레드
    Platform.runLater(() -> label.setText(result)); // UI 스레드에서 안전하게 갱신
}).start();
```

더 체계적으로는 `javafx.concurrent.Task`(Swing의 `SwingWorker`에 대응)를 써서 진행률·성공·실패를 속성으로 노출하고 바인딩한다.

---

## 10. 애니메이션

JavaFX는 애니메이션을 1급으로 지원한다. 값의 시작·끝을 지정하면 프레임 보간을 알아서 해 준다.

- **`Timeline` + `KeyFrame`** — 특정 시각에 속성이 어떤 값이 되도록 지정.
- **`TranslateTransition`, `FadeTransition`, `RotateTransition`** — 이동·페이드·회전 같은 즉석 전환 효과.

```java
FadeTransition fade = new FadeTransition(Duration.seconds(2), label);
fade.setFromValue(1.0);   // 불투명
fade.setToValue(0.0);     // 투명
fade.setCycleCount(Animation.INDEFINITE);
fade.setAutoReverse(true);
fade.play();
```

속성 바인딩과 결합하면 화면 값이 부드럽게 변하는 UI를 적은 코드로 만들 수 있다.

---

## 확인문제

> 💡 JavaFX는 GUI라서 헤드리스 컨테이너에서 실행할 수 없다. 아래 문제는 실행 대신 **개념 이해**를 확인하는 데 초점을 둔다.

**1. (개념 서술)** JavaFX의 세 핵심 구조인 `Application`, `Stage`, `Scene`의 역할을 극장 비유와 함께 각각 한 문장으로 설명하라.

<details>
<summary>정답</summary>

- **`Application`** — 앱의 진입점(극장 건물 전체). `javafx.application.Application`을 상속하고 `start(Stage)`를 재정의한다.
- **`Stage`** — 실제 창(윈도우)에 해당하는 최상위 컨테이너(무대).
- **`Scene`** — 창에 표시되는 화면 한 장(무대 위 장면)으로, 컴포넌트들의 트리인 **Scene Graph**를 담는다.

즉 `Application`이 `Stage`(창)를 받아 그 위에 `Scene`(화면)을 올리는 계층 구조다.
</details>

**2. (빈칸 채우기)** 다음 문장의 빈칸을 채워라.
JavaFX 앱은 `main`에서 `______`을(를) 호출해 런타임을 띄우며, 런타임은 자동으로 `______(Stage)` 메서드를 호출한다. 이때 UI 트리를 이루는 레이블·버튼·레이아웃 등 모든 화면 요소는 `______` 클래스를 상속한다.

<details>
<summary>정답</summary>

- 첫 번째: **`launch`** (`Application.launch`)
- 두 번째: **`start`**
- 세 번째: **`Node`**

`launch(args)`가 JavaFX 런타임을 시작하고 `start(Stage)`를 자동 호출하며, 화면에 올라가는 모든 요소는 `Node`를 상속한다.
</details>

**3. (코드 판단)** 다음 코드에서 창이 화면에 나타나지 않는다. 이유는?

```java
@Override
public void start(Stage stage) {
    Label label = new Label("안녕");
    Scene scene = new Scene(label, 400, 300);
    stage.setScene(scene);
    // stage.show(); 가 없음
}
```

<details>
<summary>정답</summary>

**`stage.show()`를 호출하지 않았기 때문**이다.

`Scene`을 만들고 `setScene`으로 창에 연결하는 것만으로는 창이 표시되지 않는다. `Stage`는 `show()`(또는 `showAndWait()`)를 호출해야 실제로 화면에 나타난다. 마지막 줄에 `stage.show();`를 추가해야 한다.
</details>

**4. (개념 서술)** JavaFX에서 이벤트를 처리하는 방식을 버튼 클릭을 예로 설명하고, 왜 람다가 잘 어울리는지 말하라.

<details>
<summary>정답</summary>

버튼 클릭은 `setOnAction`에 **`EventHandler`(함수형 인터페이스)**를 등록해 처리한다.

```java
button.setOnAction(event -> label.setText("눌렸습니다!"));
```

`EventHandler`는 추상 메서드가 하나뿐인 함수형 인터페이스이므로 **람다로 간결하게 표현**할 수 있다. 마우스는 `setOnMouseClicked`, 키 입력은 `setOnKeyPressed` 등 `setOn~` 계열 메서드가 준비되어 있다.
</details>

**5. (빈칸 채우기 / 개념)** 다음 코드는 `input`의 텍스트가 바뀌면 `mirror`가 자동으로 같은 값이 되도록 만든다. 빈칸에 들어갈 메서드 이름과, 이런 동기화 방식을 부르는 용어는?

```java
mirror.textProperty().______(input.textProperty());
```

<details>
<summary>정답</summary>

- 빈칸: **`bind`**
- 용어: **바인딩(Binding)**

JavaFX 컨트롤의 값은 단순 필드가 아니라 **`Property` 객체**(`textProperty()` 등)로 노출된다. `bind`로 두 속성을 묶으면 한쪽이 바뀔 때 다른 쪽이 자동으로 따라간다. 덕분에 "값이 바뀔 때마다 화면을 수동으로 갱신"하는 반복 코드를 줄이는 **선언적 UI**가 가능하다.
</details>

**6. (참·거짓 + 이유)** "백그라운드 스레드에서 `label.setText(result)`를 직접 호출해도 안전하다." — 참인가 거짓인가? 이유를 밝혀라.

<details>
<summary>정답</summary>

**거짓.**

JavaFX는 **UI를 JavaFX Application Thread에서만 변경**하는 규칙이 있다. 백그라운드(다른) 스레드에서 UI를 직접 건드리면 예외가 발생한다. 다른 스레드에서 UI를 바꿔야 한다면 `Platform.runLater(...)`로 작업을 UI 스레드에 넘겨야 한다.

```java
Platform.runLater(() -> label.setText(result));
```

더 체계적으로는 `javafx.concurrent.Task`를 써서 진행률·성공·실패를 속성으로 노출하고 바인딩한다.
</details>

**7. (Swing과 비교)** 컨테이너에 자식 컴포넌트를 넣는 방법이 Swing과 JavaFX에서 어떻게 다른지 코드와 함께 설명하라.

<details>
<summary>정답</summary>

- **Swing**: 컨테이너에 `add(comp)`로 직접 추가한다.
- **JavaFX**: 컨테이너의 **`getChildren()`** 리스트를 얻어 `add(...)`/`addAll(...)`로 추가한다.

```java
VBox root = new VBox(10);
root.getChildren().addAll(new Label("이름:"), new TextField(), new Button("확인"));
```

즉 JavaFX는 자식 노드를 담는 `getChildren()` 컬렉션을 노출하고, 그 컬렉션을 조작해 화면 트리를 구성한다.
</details>

**8. (Swing과 비교)** JavaFX가 Swing보다 앞선다고 평가받는 대표 기능 세 가지를 들고, 각각 한 줄로 설명하라.

<details>
<summary>정답</summary>

- **속성·바인딩(Property/Binding)** — `textProperty().bind(...)`로 값 동기화를 자동화(선언적 UI). Swing에는 기본 제공되지 않는다.
- **CSS 스타일링** — 웹처럼 `-fx-` 접두어 CSS로 외형을 코드와 분리해 꾸민다.
- **애니메이션** — `Timeline`/`KeyFrame`, `FadeTransition` 등으로 프레임 보간을 1급 지원한다.

(이 밖에 미디어 재생, 세련된 기본 컨트롤 등도 강점이다.)
</details>

**9. (코드 판단)** 다음 코드의 의도는 "2초에 걸쳐 레이블을 서서히 사라지게 한다"이다. 각 메서드가 하는 일을 설명하라.

```java
FadeTransition fade = new FadeTransition(Duration.seconds(2), label);
fade.setFromValue(1.0);
fade.setToValue(0.0);
fade.play();
```

<details>
<summary>정답</summary>

- `new FadeTransition(Duration.seconds(2), label)` — `label`을 대상으로 **2초 동안** 진행되는 페이드 전환을 만든다.
- `setFromValue(1.0)` — 시작 불투명도 **1.0(완전 불투명)**.
- `setToValue(0.0)` — 끝 불투명도 **0.0(완전 투명)**.
- `play()` — 애니메이션을 **실행**한다.

결과적으로 레이블이 2초에 걸쳐 불투명 → 투명으로 부드럽게 사라진다. (`setCycleCount`/`setAutoReverse`를 주면 반복·왕복도 가능하다.)
</details>

---

## 요약

- **JavaFX**는 Swing의 후속 현대 GUI 프레임워크로, CSS 스타일·속성 바인딩·애니메이션을 기본 제공한다.
- ⚠️ **JDK에 미포함** — 별도 **JavaFX SDK/의존성**을 모듈 경로 또는 빌드 도구로 추가해야 한다.
- 구조는 **Application → Stage(창) → Scene(화면)**, 화면 요소는 모두 `Node`.
- 레이아웃은 `VBox`/`HBox`/`BorderPane`/`GridPane`, 자식은 `getChildren().add(...)`로 추가.
- 이벤트는 `setOnAction` 등 `setOn~` 핸들러로 처리.
- **속성·바인딩**(`textProperty().bind(...)`)으로 값 동기화를 자동화한다.
- UI 변경은 **JavaFX Application Thread**에서만 — 백그라운드에서는 `Platform.runLater`(또는 `Task`).
- 스타일은 **CSS**(`-fx-` 접두어), 다이얼로그는 `Alert`, 애니메이션은 `Timeline`/`Transition`.
- ⚠️ GUI라 **헤드리스 컨테이너 실행 불가**, JavaFX SDK를 갖춘 **로컬 데스크톱**에서 실행한다.
