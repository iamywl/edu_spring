# 부록 01. Swing — 자바 데스크톱 GUI

콘솔 프로그램은 텍스트로만 소통한다. 하지만 사용자가 마우스로 버튼을 누르고, 창을 끌어 옮기고, 표에서 행을 선택하는 "그래픽 사용자 인터페이스(GUI)"를 만들려면 별도의 도구가 필요하다. **Swing**은 자바 표준 라이브러리(`javax.swing`)에 포함된 데스크톱 GUI 툴킷으로, JDK만 있으면 별도 설치 없이 창·버튼·메뉴·표를 그릴 수 있다.

이 부록은 Swing의 핵심 개념(EDT, 컨테이너, 레이아웃, 이벤트, 컴포넌트, 다이얼로그, 2D 그래픽스)을 개념과 짧은 코드로 정리한다. 문법을 완벽히 외우기보다 "Swing 앱은 어떤 구조로 돌아가는가"를 잡는 것이 목표다.

> ⚠️ **실행 환경 주의**
> Swing은 화면(디스플레이)이 있어야 창을 띄운다. 이 교육 프로젝트가 도는 **Docker / CI 같은 헤드리스(headless) 컨테이너에는 디스플레이가 없어 실행되지 않는다** (`java.awt.HeadlessException` 발생). 아래 코드들은 **본인의 로컬 데스크톱**(Windows/macOS/Linux 데스크톱 환경)에서 `javac`/`java`로 직접 실행해야 창이 뜬다. `./run.sh`나 컨테이너 안에서는 동작하지 않는다.

> 📘 Swing은 오래된 툴킷이라 실무 신규 프로젝트는 [부록 02. JavaFX](부록-02-JavaFX.md)를 쓰는 경우가 많다. 다만 개념(EDT, 이벤트 리스너, 레이아웃)은 두 툴킷이 공유하므로 Swing으로 기초를 잡아 두면 JavaFX 학습이 쉬워진다.

---

## 1. Swing 소개와 첫 창

Swing은 AWT(더 오래된 툴킷) 위에 얹혀 있지만, 버튼·표 같은 컴포넌트를 자바가 직접 그리는 "경량(lightweight)" 방식이라 운영체제가 달라도 화면이 거의 동일하게 나온다. 클래스 이름 앞에 대부분 **`J`**가 붙는다(`JFrame`, `JButton`, `JLabel` …).

가장 기본이 되는 것은 창을 나타내는 `JFrame`이다.

```java
import javax.swing.*;

public class HelloSwing {
    public static void main(String[] args) {
        // 모든 UI 생성/수정은 EDT 위에서 하는 것이 원칙 (2장 참고)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("첫 Swing 창");   // 제목
            frame.setSize(400, 300);                     // 창 크기
            frame.setLocationRelativeTo(null);           // 화면 중앙
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // X 누르면 종료
            frame.add(new JLabel("안녕하세요, Swing!", SwingConstants.CENTER));
            frame.setVisible(true);                       // 화면에 표시
        });
    }
}
```

`setDefaultCloseOperation`을 빠뜨리면 창을 닫아도 프로세스가 살아 있으니 반드시 지정한다.

---

## 2. EDT — 이벤트 디스패칭 스레드

Swing의 가장 중요한 규칙은 **"모든 UI 접근은 EDT(Event Dispatch Thread) 위에서 한다"**이다. EDT는 Swing이 내부적으로 돌리는 단 하나의 UI 전용 스레드로, 버튼 클릭·다시 그리기 같은 모든 이벤트를 순서대로 처리한다.

- Swing 컴포넌트는 **스레드 안전(thread-safe)하지 않다.** 여러 스레드가 동시에 UI를 건드리면 화면이 깨지거나 멈춘다.
- 그래서 `main`에서 UI를 만들 때도 `SwingUtilities.invokeLater(...)`로 EDT에 작업을 넘긴다.
- 반대로, 시간이 오래 걸리는 작업(네트워크·파일 읽기)을 **EDT에서 직접 하면 화면이 얼어붙는다.** 이런 작업은 `SwingWorker`로 백그라운드 스레드에 맡기고, 결과만 EDT로 돌려받는다.

```java
new SwingWorker<String, Void>() {
    @Override protected String doInBackground() throws Exception {
        Thread.sleep(2000);        // 무거운 작업(백그라운드 스레드에서 실행)
        return "완료";
    }
    @Override protected void done() throws Exception {
        label.setText(get());      // 이 코드는 EDT에서 안전하게 실행됨
    }
}.execute();
```

핵심: **UI 갱신은 EDT, 무거운 작업은 백그라운드.** 이 원칙은 JavaFX의 `Platform.runLater`와 정확히 대응된다.

---

## 3. 컨테이너와 레이아웃 매니저

`JFrame`, `JPanel` 같은 **컨테이너**는 다른 컴포넌트를 담는 상자다. 컴포넌트를 담을 때 좌표를 일일이 지정하지 않고, **레이아웃 매니저**에게 배치를 맡기는 것이 Swing 방식이다. 창 크기가 바뀌어도 알아서 재배치되기 때문이다.

주요 레이아웃 세 가지:

- **`BorderLayout`** (JFrame 기본): 상/하/좌/우/중앙 다섯 구역. 중앙이 남는 공간을 다 차지한다.
- **`FlowLayout`** (JPanel 기본): 왼쪽부터 한 줄로 흐르듯 배치, 자리 없으면 다음 줄로.
- **`GridLayout`**: 지정한 행×열 격자에 균등 배치. 계산기 버튼판처럼.

```java
JPanel panel = new JPanel(new BorderLayout());
panel.add(new JButton("위"),   BorderLayout.NORTH);
panel.add(new JButton("가운데"), BorderLayout.CENTER);
panel.add(new JButton("아래"),  BorderLayout.SOUTH);

JPanel grid = new JPanel(new GridLayout(2, 3)); // 2행 3열
for (int i = 1; i <= 6; i++) grid.add(new JButton(String.valueOf(i)));
```

복잡한 화면은 `JPanel` 안에 또 `JPanel`을 넣어 레이아웃을 **중첩**해 구성한다.

---

## 4. 이벤트 처리 — ActionListener

버튼을 눌렀을 때 무언가 일어나게 하려면 컴포넌트에 **리스너(listener)**를 등록한다. 버튼 클릭·메뉴 선택 등 "행동"에 반응하는 것이 `ActionListener`다. 사용자가 버튼을 누르면 EDT가 등록된 `actionPerformed`를 호출한다.

```java
JButton button = new JButton("눌러보세요");
JLabel  label  = new JLabel("아직 안 눌림");

// 람다로 간결하게 (ActionListener는 함수형 인터페이스)
button.addActionListener(e -> label.setText("눌렸습니다! " + e.getActionCommand()));
```

이 밖에 마우스 움직임은 `MouseListener`, 키 입력은 `KeyListener`, 창 닫힘은 `WindowListener`로 처리한다. 이벤트 → 리스너 → 콜백 메서드라는 구조는 모든 GUI 툴킷에 공통이다.

---

## 5. 주요 컴포넌트

Swing이 제공하는 대표 컴포넌트들이다. 모두 컨테이너에 `add`해서 사용한다.

- **`JButton`** — 클릭 버튼. `addActionListener`로 반응.
- **`JTextField` / `JTextArea`** — 한 줄 / 여러 줄 텍스트 입력. `getText()`로 값을 읽는다.
- **`JLabel`** — 편집 불가 텍스트/아이콘 표시.
- **`JCheckBox` / `JRadioButton`** — 체크박스 / 라디오 버튼(`ButtonGroup`으로 묶으면 하나만 선택).
- **`JComboBox`** — 드롭다운 선택 목록.
- **`JList`** — 여러 항목을 세로로 나열, 다중 선택 가능.
- **`JTable`** — 행/열 표. 데이터는 `TableModel`이 관리(모델-뷰 분리).
- **`JTree`** — 폴더 탐색기 같은 계층 트리.

```java
JTextField field = new JTextField(20);            // 20칸 너비
JList<String> list = new JList<>(new String[]{"사과", "바나나", "포도"});

// JTable: 데이터(2차원 배열) + 컬럼명
String[] cols = {"이름", "점수"};
Object[][] rows = {{"철수", 90}, {"영희", 85}};
JTable table = new JTable(rows, cols);
JScrollPane scroll = new JScrollPane(table);      // 표는 보통 스크롤팬에 담는다
```

`JList`·`JTable`·`JTree`처럼 데이터가 많은 컴포넌트는 스크롤을 위해 `JScrollPane`으로 감싸는 것이 관례다.

---

## 6. 메뉴와 툴바

창 상단의 메뉴 막대와 아이콘 툴바다.

- **`JMenuBar`** — 창 맨 위 메뉴 막대. `frame.setJMenuBar(bar)`로 부착.
- **`JMenu`** — "파일", "편집" 같은 메뉴 하나.
- **`JMenuItem`** — 메뉴 안의 실제 항목(클릭 가능). `ActionListener` 등록.
- **`JToolBar`** — 버튼을 가로로 늘어놓는 도구 막대. 드래그해 떼어낼 수도 있다.

```java
JMenuBar menuBar = new JMenuBar();
JMenu fileMenu = new JMenu("파일");
JMenuItem openItem = new JMenuItem("열기");
openItem.addActionListener(e -> System.out.println("열기 클릭"));
fileMenu.add(openItem);
menuBar.add(fileMenu);
frame.setJMenuBar(menuBar);

JToolBar toolBar = new JToolBar();
toolBar.add(new JButton("새로"));
toolBar.add(new JButton("저장"));
frame.add(toolBar, BorderLayout.NORTH);
```

---

## 7. 다이얼로그 — JOptionPane

간단한 알림·확인·입력 창은 `JOptionPane`의 정적 메서드로 한 줄이면 띄운다.

```java
// 알림
JOptionPane.showMessageDialog(frame, "저장되었습니다.");

// 예/아니오 확인
int answer = JOptionPane.showConfirmDialog(frame, "정말 삭제할까요?", "확인",
                                           JOptionPane.YES_NO_OPTION);
if (answer == JOptionPane.YES_OPTION) { /* 삭제 진행 */ }

// 문자열 입력받기
String name = JOptionPane.showInputDialog(frame, "이름을 입력하세요");
```

더 복잡한 사용자 정의 대화상자가 필요하면 `JDialog`를 직접 만들어 컴포넌트를 배치한다.

---

## 8. 2D 그래픽스 — paintComponent

직접 도형·선·이미지를 그리려면 `JPanel`을 상속해 **`paintComponent(Graphics g)`**를 재정의한다. 화면을 다시 그려야 할 때마다 Swing이 이 메서드를 EDT에서 호출한다. 우리가 직접 부르지 않고, 다시 그리라고 요청할 때는 `repaint()`를 호출한다.

```java
class DrawPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);           // 배경 지우기(필수)
        Graphics2D g2 = (Graphics2D) g;    // 고급 2D 기능
        g2.setColor(Color.BLUE);
        g2.fillRect(20, 20, 100, 60);      // 채운 사각형
        g2.setColor(Color.RED);
        g2.drawOval(150, 20, 80, 80);      // 원 테두리
        g2.drawString("Graphics!", 20, 130);
    }
}
```

`Graphics2D`는 선 굵기, 안티앨리어싱, 회전·이동(변환) 같은 정교한 그리기를 지원한다. 애니메이션은 `javax.swing.Timer`로 일정 간격마다 좌표를 바꾸고 `repaint()`를 호출해 구현한다.

---

## 요약

- **Swing**은 JDK에 포함된 데스크톱 GUI 툴킷으로, 클래스 이름 앞에 대부분 `J`가 붙는다.
- **EDT(이벤트 디스패칭 스레드)** 위에서만 UI를 다뤄야 한다. UI 생성은 `SwingUtilities.invokeLater`, 무거운 작업은 `SwingWorker`로 백그라운드 처리한다.
- **레이아웃 매니저**(`BorderLayout`/`FlowLayout`/`GridLayout`)가 컴포넌트를 자동 배치하며, `JPanel` 중첩으로 복잡한 화면을 구성한다.
- **이벤트**는 리스너(`ActionListener` 등)로 처리하고, 콜백 메서드에서 반응 로직을 작성한다.
- 주요 컴포넌트: `JButton`·`JTextField`·`JList`·`JTable`·`JTree`, 메뉴는 `JMenuBar`/`JMenu`/`JMenuItem`, 툴바는 `JToolBar`.
- 간단한 대화상자는 `JOptionPane`, 직접 그리기는 `JPanel.paintComponent(Graphics)`.
- ⚠️ Swing 앱은 디스플레이가 필요하므로 **헤드리스 컨테이너에서 실행 불가**, 반드시 **로컬 데스크톱**에서 실행한다.
