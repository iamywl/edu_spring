#!/usr/bin/env bash
#
# observe_http.sh — 네트워크 계층을 '눈으로' 관찰하는 실습 스크립트
# ------------------------------------------------------------------
# CS_네트워크_개념서.md 를 읽고 나서 이 스크립트를 돌리면,
# 책에서 설명한 DNS / TCP / TLS / HTTP 가 실제로 어떻게 생겼는지 직접 봅니다.
#
# 설계 원칙(안전):
#   - 읽기 전용(read-only). 아무것도 설치/변경/삭제하지 않습니다.
#   - 공개 테스트 엔드포인트(example.com)만 조회합니다.
#   - 모든 명령에 타임아웃/입력차단(</dev/null 등)을 걸어 절대 멈추지 않습니다.
#   - 도구가 없으면 건너뜁니다(graceful skip). 하나도 없어도 안전하게 끝납니다.
#   - 인터넷 연결이 필요합니다. 오프라인이면 각 단계가 실패 메시지를 내고 계속 진행합니다.
#
# 사용법:  bash labs/observe_http.sh
# ------------------------------------------------------------------

# set -e 는 일부러 쓰지 않는다: 개별 도구가 실패해도 다음 단계로 넘어가야 하므로.
set -u

DOMAIN="example.com"
HTTP_HOST="example.com"   # 평문 HTTP(80) 관찰용
CURL_MAXTIME=10           # curl 최대 실행 시간(초)

# ── 색/헤더 유틸 ─────────────────────────────────────────────
if [ -t 1 ]; then
  BOLD=$'\033[1m'; CYAN=$'\033[36m'; YELLOW=$'\033[33m'; GREEN=$'\033[32m'; DIM=$'\033[2m'; RST=$'\033[0m'
else
  BOLD=""; CYAN=""; YELLOW=""; GREEN=""; DIM=""; RST=""
fi

section() {
  echo ""
  echo "${CYAN}${BOLD}============================================================${RST}"
  echo "${CYAN}${BOLD} $1${RST}"
  echo "${CYAN}${BOLD}============================================================${RST}"
}

note()  { echo "${YELLOW}▶ $1${RST}"; }
have()  { command -v "$1" >/dev/null 2>&1; }
skip()  { echo "${DIM}  (건너뜀) '$1' 명령이 없어 이 단계를 생략합니다.${RST}"; }

# ── 인트로 ───────────────────────────────────────────────────
echo "${BOLD}"
echo "  ┌───────────────────────────────────────────────────────┐"
echo "  │  네트워크 스택 관찰 실습 (observe_http.sh)             │"
echo "  │  개념서: docs/CS_네트워크_개념서.md 와 함께 보세요.    │"
echo "  └───────────────────────────────────────────────────────┘"
echo "${RST}"
echo "  대상 도메인: ${GREEN}${DOMAIN}${RST}  (공개 테스트 사이트, 읽기 전용 조회)"
echo "  ${DIM}인터넷 연결이 필요합니다. 오프라인이면 각 단계가 실패 메시지를 냅니다.${RST}"

# ── 사전 점검: 네트워크 도달 여부(참고용, 실패해도 계속) ──────
section "0. 사전 점검 — 네트워크가 살아있나?"
if have curl; then
  if curl -s -m 5 -o /dev/null "https://${DOMAIN}"; then
    echo "  ${GREEN}네트워크 OK${RST}: https://${DOMAIN} 응답 확인."
  else
    echo "  ${YELLOW}주의${RST}: https://${DOMAIN} 에 닿지 못했습니다. 오프라인일 수 있습니다."
    echo "        아래 단계들은 실패 메시지를 낼 수 있지만, 스크립트는 안전하게 끝납니다."
  fi
else
  echo "  ${DIM}curl 이 없어 사전 점검을 건너뜁니다.${RST}"
fi

# ══════════════════════════════════════════════════════════════
# STEP 1. DNS — 이름을 IP로  (개념서 Ch4)
# ══════════════════════════════════════════════════════════════
section "1. DNS — 이름을 주소로 (개념서 Ch4)"
note "'${DOMAIN}' 이라는 이름이 어떤 IP(A 레코드)로 번역되는지, 그리고 TTL(캐시 수명)을 봅니다."

if have dig; then
  echo ""
  echo "  ${BOLD}\$ dig +noall +answer ${DOMAIN} A${RST}"
  # +noall +answer : 정답 섹션만 간결하게. (TTL 숫자가 각 줄에 보인다)
  dig +noall +answer "${DOMAIN}" A </dev/null 2>&1 | sed 's/^/    /'
  echo ""
  echo "  ${BOLD}\$ dig +noall +answer ${DOMAIN} AAAA${RST}   ${DIM}(IPv6 주소)${RST}"
  dig +noall +answer "${DOMAIN}" AAAA </dev/null 2>&1 | sed 's/^/    /'
  echo ""
  echo "  ${DIM}읽는 법:  이름   TTL(초)   IN   A     IP주소${RST}"
  echo "  ${DIM}          TTL 숫자가 '이 답을 몇 초간 캐시해도 되는가'입니다(§4.3).${RST}"
elif have nslookup; then
  note "dig 가 없어 nslookup 으로 대체합니다."
  echo "  ${BOLD}\$ nslookup ${DOMAIN}${RST}"
  nslookup "${DOMAIN}" </dev/null 2>&1 | sed 's/^/    /'
else
  skip "dig/nslookup"
fi

# ══════════════════════════════════════════════════════════════
# STEP 2. TCP + TLS + HTTP 헤더  (curl -v, 개념서 Ch3/5/6)
# ══════════════════════════════════════════════════════════════
section "2. TCP·TLS·HTTP 한눈에 — curl -v (개념서 Ch3/5/6)"
note "curl 의 상세 로그(-v)에서 '연결→TLS 핸드셰이크→요청/응답 헤더'를 한 번에 봅니다."

if have curl; then
  echo ""
  echo "  ${BOLD}\$ curl -v -s -m ${CURL_MAXTIME} -o /dev/null https://${DOMAIN}${RST}"
  echo "  ${DIM}( -o /dev/null : 바디(HTML)는 버리고 '헤더/핸드셰이크 로그'만 봅니다 )${RST}"
  echo ""
  # -v 로그는 stderr 로 나온다. 2>&1 로 합쳐서 캡처해 해설을 붙인다.
  # </dev/null 로 입력 차단, -m 으로 시간 제한 → 멈춤 방지.
  curl_log="$(curl -v -s -m "${CURL_MAXTIME}" -o /dev/null "https://${DOMAIN}" </dev/null 2>&1)"

  echo "  ${GREEN}[가) TCP 연결 성립 — §3.3 3-way handshake 완료 지점]${RST}"
  echo "${curl_log}" | grep -E "Trying|Connected to" | sed 's/^/    /' || echo "    (해당 로그 없음)"

  echo ""
  echo "  ${GREEN}[나) TLS 핸드셰이크 — §6.2, 인증서/버전 — §6.3]${RST}"
  echo "${curl_log}" | grep -Ei "SSL connection|TLS|handshake|certificate|subject:|issuer:|ALPN" | sed 's/^/    /' || echo "    (해당 로그 없음)"

  echo ""
  echo "  ${GREEN}[다) 우리가 보낸 HTTP 요청 헤더 — §5.1 (> 로 시작)]${RST}"
  echo "${curl_log}" | grep -E "^> " | sed 's/^/    /' || echo "    (해당 로그 없음)"

  echo ""
  echo "  ${GREEN}[라) 서버가 보낸 HTTP 응답 상태·헤더 — §5.1 (< 로 시작)]${RST}"
  echo "${curl_log}" | grep -E "^< " | sed 's/^/    /' || echo "    (해당 로그 없음)"

  echo ""
  echo "  ${DIM}'> ' 는 요청(내가 보냄), '< ' 는 응답(서버가 보냄). 첫 '< HTTP/..' 줄이 상태 라인입니다.${RST}"
else
  skip "curl"
fi

# ══════════════════════════════════════════════════════════════
# STEP 3. 인증서 체인 / TLS 버전  (openssl, 개념서 §6.3)
# ══════════════════════════════════════════════════════════════
section "3. 인증서 체인 & TLS 버전 — openssl (개념서 §6.3)"
note "서버가 제시하는 인증서 체인(누가 이 서버를 보증하는가)과 TLS 버전을 봅니다."

if have openssl; then
  echo ""
  echo "  ${BOLD}\$ openssl s_client -connect ${DOMAIN}:443 -servername ${DOMAIN} </dev/null${RST}"
  echo "  ${DIM}( </dev/null : 즉시 입력 종료로 대화형 대기(멈춤)를 방지 )${RST}"
  echo ""
  # -servername : SNI. 한 IP가 여러 도메인을 호스팅하므로 어느 도메인인지 알려줘야 함(§5.1 Host와 같은 이유).
  # </dev/null 로 즉시 종료. -brief 는 일부 버전에만 있어, 없으면 전체 출력에서 핵심만 grep.
  ssl_log="$(echo | openssl s_client -connect "${DOMAIN}:443" -servername "${DOMAIN}" 2>/dev/null)"

  echo "  ${GREEN}[가) 인증서 체인 — leaf → 중간 CA → (root) 로 이어지는 신뢰 사슬 §6.3]${RST}"
  echo "${ssl_log}" | grep -E "^ *[0-9]+ (s|i):" | sed 's/^/    /' || \
    echo "${ssl_log}" | grep -E "s:|i:" | head -20 | sed 's/^/    /' || echo "    (체인 정보 없음)"
  echo "    ${DIM}s: = subject(이 인증서의 주인),  i: = issuer(이 인증서를 서명한 상위 기관)${RST}"

  echo ""
  echo "  ${GREEN}[나) 협상된 TLS 버전 / 암호 스위트 — §6.2]${RST}"
  echo "${ssl_log}" | grep -Ei "Protocol *:|Cipher *:|Server Temp Key|Verify return code" | sed 's/^/    /' || echo "    (정보 없음)"
  echo "    ${DIM}'Verify return code: 0 (ok)' 이면 인증서 검증 통과(§6.3).${RST}"
else
  skip "openssl"
fi

# ══════════════════════════════════════════════════════════════
# STEP 4. 날것의 HTTP — 평문 TCP로 요청 바이트 직접 보기  (개념서 §5.1)
# ══════════════════════════════════════════════════════════════
section "4. 날것의 HTTP — 평문 TCP로 요청 원문 보기 (개념서 §5.1)"
note "암호화 없는 http(80)로 요청 텍스트를 그대로 보냅니다. HTTP가 '텍스트'임을 확인합니다."
note "이 단계는 Java 데모(RawHttpClient.java)가 하는 일과 동일합니다."

# printf 로 CRLF(\r\n) 로 끝나는 HTTP/1.1 요청을 만들고,
# nc(netcat) 로 평문 TCP 80 포트에 흘려보낸다. -w 로 타임아웃 → 멈춤 방지.
raw_request=$(printf 'GET / HTTP/1.1\r\nHost: %s\r\nUser-Agent: observe_http.sh\r\nConnection: close\r\n\r\n' "${HTTP_HOST}")

echo ""
echo "  ${BOLD}보낼 요청 원문(raw request):${RST}"
printf '%s' "${raw_request}" | sed 's/^/    | /'
echo "    ${DIM}(각 줄 끝의 \\r\\n, 그리고 마지막 빈 줄이 §5.1의 '헤더 끝' 신호)${RST}"
echo ""

if have nc; then
  echo "  ${BOLD}\$ printf '<요청>' | nc -w 8 ${HTTP_HOST} 80  (응답 상태라인+헤더)${RST}"
  # nc 구현마다 옵션이 조금씩 달라 -w(타임아웃)만 사용. 응답에서 상태라인+헤더까지만 표시.
  resp="$(printf '%s' "${raw_request}" | nc -w 8 "${HTTP_HOST}" 80 2>/dev/null)"
  if [ -n "${resp}" ]; then
    # 빈 줄(헤더 끝) 전까지 = 상태 라인 + 헤더. awk 로 그 지점까지만.
    echo "${resp}" | awk 'BEGIN{h=1} { if(h==0) next; print "    " $0; if($0 ~ /^\r?$/){h=0; print "    ... (이하 바디 생략) ..."} }'
  else
    echo "    ${DIM}응답이 비었습니다(오프라인이거나 서버가 80을 차단). 개념 확인엔 지장 없습니다.${RST}"
  fi
elif have curl; then
  note "nc 가 없어 curl 로 대체합니다(평문 http 헤더만)."
  echo "  ${BOLD}\$ curl -sI -m ${CURL_MAXTIME} http://${HTTP_HOST}/${RST}"
  curl -sI -m "${CURL_MAXTIME}" "http://${HTTP_HOST}/" </dev/null 2>&1 | sed 's/^/    /' || echo "    (실패)"
else
  skip "nc/curl"
fi

# ── 마무리 ───────────────────────────────────────────────────
section "정리"
echo "  방금 계층을 아래에서 위로 훑었습니다:"
echo "    1) ${BOLD}DNS${RST}    이름 → IP (A/AAAA, TTL)              ... Ch4"
echo "    2) ${BOLD}TCP${RST}    'Connected to' = 3-way handshake 완료 ... Ch3"
echo "    3) ${BOLD}TLS${RST}    핸드셰이크·인증서 체인·버전            ... Ch6"
echo "    4) ${BOLD}HTTP${RST}   요청/응답이 '사람이 읽는 텍스트'임      ... Ch5"
echo ""
echo "  ${GREEN}다음:${RST} Java 로 소켓을 직접 열어 같은 일을 해보세요."
echo "    ${DIM}javac -d /tmp/netout ../src/main/java/com/edu/network/RawHttpClient.java${RST}"
echo "    ${DIM}java  -cp /tmp/netout com.edu.network.RawHttpClient${RST}"
echo ""
echo "  ${DIM}이 스크립트는 아무것도 변경하지 않았습니다(읽기 전용).${RST}"
