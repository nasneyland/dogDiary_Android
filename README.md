## dogDiary_Android
2020년 12월 23일에 플레이스토어에 출시한 반려견 다이어리 Android 프로젝트 입니다.<br/>
[플레이스토어 바로가기](https://play.google.com/store/apps/details?id=com.najin.dogdiary&pli=1)<br/>
[노션 바로가기](https://najinland.notion.site/a87b6517f55b4b9f8a9a5d67bc61689c)<br/>

<a href="https://github.com/nasneyland/dogDiary_iOS"><img src="https://img.shields.io/badge/iOS 프로젝트-000000?style=flat-square&logo=Apple&logoColor=white"/></a> <a href="https://github.com/nasneyland/dogDiary_Android"><img src="https://img.shields.io/badge/Android 프로젝트-000000?style=flat-square&logo=Android&logoColor=white"/></a> <a href="https://github.com/nasneyland/dogDiary_Backend"><img src="https://img.shields.io/badge/Backend 프로젝트-000000?style=flat-square&logo=Django&logoColor=white"/></a>

### 디자인 패턴
- MVC 패턴 사용
- 컨트롤러에서 뷰를 구성하고, 데이터 통신으로 받아온 객체들을 모델에 저장해 관리
- 페이지 당 한 컨트롤러를 구성하여 모든 함수를 컨트롤러에 구현
- 단점 : 코드가 너무 길어짐 -> 다른 프로젝트에서는 API 선언을 다른 페이지에 분리하고 컨트롤러에서는 호출만 하는 형태로 변경
- 결론 : MVC 패턴에서도 충분히 코드의 길이를 줄일 수 있는데 한 페이지에 많은 코드를 구성하였음 -> 이후에는 선언과 호출을 더욱 분리하도록 구현함

### 레이아웃 구성
- Storyboard에서 Auto Layout을 구현하는 방법 사용
- 장점 : 직관적인 레이아웃 구현 가능, 빌드 없이 레이아웃 수정을 바로 확인 가능
- 단점 : 빌드 시간이 길어짐, 스토리보드에서 지원하지 않는 옵션은 부분적으로 코드로 구현 -> 통일성이 떨어짐
- 결론 : 애플에서도 코들 구현하는 것을 권장하고 있고, 장점보다는 단점이 훨씬 많기 때문에 이후 다음 프로젝트 부터는 **SnapKit**을 이용해 구현

### 데이터 통신
- Alamofire을 이용한 데이터 통신 (get, post, put, delete)
- Alamofire의 multipartFormData을 이용하여 기기 라이브러리으 이미지를 서버로 전송

### 계정 관리
- 다이어리 특성 상 가입/로그인 절차가 꼭 필요 -> 핸드폰 SMS 인증 절차 도입
- 당근마켓의 SMS 인증 로그인 방식을 벤치마킹하여 각종 유효성검사, 타이머기능, 인증횟수제한 등 구현함
- 인증 시 인증횟수르 알려주는 팝업 (1초간 플로팅 후 사라짐) 구현
- 핸드폰 번호가 바뀐 경우를 고려해 이메일 로그인 기능도 추가구현 -> 설정에서 미리 복구용 이메일을 등록 후 로그인 가능

### 주요 기능
- 산책기록 : **NaverMap** 라이브러리를 이용하여 마커, 클릭이벤트, 이동경로 등 커스텀하여 산책 기록 기능 구현, 산책 특성 상 화면이 꺼진 상태에서도 산책을 기록해야 하기 때문에 BackgroundMode-Location updates 사용
- 캘린더 : **FSCalendar** 라이브러리를 이용하여 달력 구현, 네이버 캘린더를 벤치마킹하여 셀을 커스텀하여 구현
- 통계관리 : Progress View를 이용한 비율 그래프 구현, Charts를 이용한 선 그래프 구현
- 광고집행 : GoogleMobileAds를 이용하여 어플 내 배너광고, 전면광고 삽입











