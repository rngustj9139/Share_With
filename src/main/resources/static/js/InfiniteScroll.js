var defaultArticlePaginationSize = 5; // 한 번 요청으로 가져올 게시글의 개수
var pageNum = 0;

function detectBottom() { // 유저가 스크롤을 맨 아래로 내렸는지 탐지
    var scrollTop = $(window).scrollTop(); // 세로로 스크롤되어 내려온 위치, $를 이용하려면 jquery를 import 해야한다(CDN)
    var innerHeight = $(window).innerHeight(); // 브라우저에 표시된 높이
    var scrollHeight = $('body').prop('scrollHeight'); // 페이지 전체 높이

    if (scrollTop + innerHeight >= scrollHeight) {
        return true;
    } else {
        return false;
    }
}

addEventListener("scroll", e => { // 수직 스크롤이 맨 아래에 올 경우를 감지한다. 스크롤이 맨 끝으로 내려온 순간에 페이지네이션을 위한 데이터를 구해서 서버로 ajax 콜을 날린다.이 때 현재 게시물의 id 중 가장 작은 값을 추려내고, 그 id 값과 가져올 게시물의 개수를 ajax 콜에 실어서 요청한다.
    if (detectBottom() === true) {
        pageNum += 1
        console.log("defaultArticlePaginationSize: " + defaultArticlePaginationSize);

        let articleCards = document.querySelectorAll('.item-card');

        let firstArticleId = articleCards[0].id

        let lastArticleId = Array.from(articleCards).map(function (card) {
            console.log("card.id: " + card.id);
            return parseInt(card.id, 10);
        }).reduce(function (previous, current) {
            return previous > current ? current : previous;
        }); // 현재 DOM에 그려진 게시물 중 가장 작은 id 값을 추려낸다.

        console.log("lastArticleId: " + lastArticleId);
        console.log("articleCards size: " + articleCards.length);

        if (document.getElementById('itemType').value.length > 1) {
            articleSearch = document.getElementById('itemType').value;
            console.log("articleSearch : " + articleSearch);
            ajaxLogic(lastArticleId, pageNum, articleSearch);
        } else {
            ajaxLogic2(lastArticleId, pageNum);
        }
    }

});

function makeOneArticle(article) {
    let itemCard = document.createElement('div');
    itemCard.className = 'item-card';
    itemCard.id = article.id;
    itemCard.onclick = function onclickItemCard() {
        console.log("무한 스크롤링 article url: " + '/articles/' + (itemCard.id).toString());
        window.location.href = "http://localhost:8080/articles/" + (itemCard.id).toString();
    }

    if (article['limitPersonnel'] === article['applicationPersonnel']) {
        let applicationDone = document.createElement('div');
        applicationDone.className = 'overlay';
        applicationDone.innerText = '모집 완료';
        itemCard.appendChild(applicationDone);
    }

    const image = new Image();
    image.className = 'item-img';
    // console.log("이미지 파일: " + itemCard["imageFiles"][0]['storeFileName']);
    if(article && article['imageFiles']) {
        // console.log("이미지 파일: " + itemCard["imageFiles"][0]['storeFileName']);
        image.src = '/article/images/' + article['imageFiles'][0]['storeFileName'];
    }
    itemCard.appendChild(image);

    let title = document.createElement('div');
    title.className = 'item-content';
    let titleText= document.createElement('p');
    titleText.innerText = article['title'];
    title.appendChild(titleText);
    itemCard.appendChild(title);

    let applicantArea = document.createElement('div');
    applicantArea.className = 'applicant-area';
    let location = document.createElement('div')
    let specificLocation = document.createElement('p');
    specificLocation.innerText = article['location'];
    location.appendChild(specificLocation);
    applicantArea.appendChild(location);
    let personnel = document.createElement('div');
    let specificPersonnel = document.createElement('p');
    specificPersonnel.innerText = article['applicationPersonnel'] + '/' + article['limitPersonnel']
    specificPersonnel.style.display = 'inline';
    personnel.appendChild(specificPersonnel);
    let applicantImg = document.createElement('img');
    applicantImg.className = 'applicant-img';
    applicantImg.src = '/assets/applicant.png';
    personnel.appendChild(applicantImg);
    applicantArea.appendChild(personnel);
    itemCard.appendChild(applicantArea);

    // console.log("스크롤링으로 새롭게 추가된 소분글의 id: " + article['id']);

    return itemCard;
}

// ajax 로직 실행 시작 (매개변수 3개인 경우)
function ajaxLogic(lastArticleId, pageNum, articleSearch) {
    $.ajax({
        async : false, // 비동기가 아닌 동기방식을 이용하겠다 => 만약 id가 16, 15인 글 두개가 응답으로 와야하는데 그전에 또 16, 15이 글 두개를 달라고 또 요청을 보내서 id가 16, 15인 글이 여러개인 이슈 발생 방지
        url: '/api/articles',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            "articleSearch": articleSearch,
            "lastArticleId": lastArticleId,
            "size": defaultArticlePaginationSize,
            "pageNum": pageNum,
        }),
        success: function () {
            console.log("데이터 전송 성공");
        },
        error: function (error) {
            console.log("데이터 전송 에러");
        }
    })
        .done(function (result) {
            console.log("Ajax 응답 데이터: " + result);
            console.log("첫번째 응답 데이터의 id: " + result[0]['id']);
            console.log("첫번째 응답 데이터의 limitPersonnel: " + result[0]['limitPersonnel']);
            console.log("첫번째 응답 데이터 JSON: " + JSON.stringify(result));
            console.log("첫번째 응답 데이터 JSON의 storeFileName: " + JSON.stringify(result[0]['imageFiles'][0]['storeFileName']));
            console.log("응답 데이터 사이즈: " + result.length);

            if (result.length === 0) {
                return;
            }

            let cardsContainer = document.getElementById("cards-container");
            console.log("cardsContainer: " + cardsContainer);

            for (var i = 0; i < result.length; i++) {
                cardsContainer.appendChild(makeOneArticle(result[i]));
            }
        });
    }
// ajax 로직 실행 끝 (매개변수 3개인 경우)

// ajax 로직 실행 시작 (매개변수 2개인 경우)
function ajaxLogic2(lastArticleId, pageNum) {
    $.ajax({
        async : false, // 비동기가 아닌 동기방식을 이용하겠다 => 만약 id가 16, 15인 글 두개가 응답으로 와야하는데 그전에 또 16, 15이 글 두개를 달라고 또 요청을 보내서 id가 16, 15인 글이 여러개인 이슈 발생 방지
        url: '/api/articles',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            "lastArticleId": lastArticleId,
            "size": defaultArticlePaginationSize,
            "pageNum": pageNum,
        }),
        success: function () {
            console.log("데이터 전송 성공");
        },
        error: function (error) {
            console.log("데이터 전송 에러");
        }
    })
        .done(function (result) {
            console.log("Ajax 응답 데이터: " + result);
            console.log("첫번째 응답 데이터의 id: " + result[0]['id']);
            console.log("첫번째 응답 데이터의 limitPersonnel: " + result[0]['limitPersonnel']);
            console.log("첫번째 응답 데이터 JSON: " + JSON.stringify(result));
            console.log("첫번째 응답 데이터 JSON의 storeFileName: " + JSON.stringify(result[0]['imageFiles'][0]['storeFileName']));
            console.log("응답 데이터 사이즈: " + result.length);

            if (result.length === 0) {
                return;
            }

            let cardsContainer = document.getElementById("cards-container");
            console.log("cardsContainer: " + cardsContainer);

            for (var i = 0; i < result.length; i++) {
                cardsContainer.appendChild(makeOneArticle(result[i]));
            }
        });
}
// ajax 로직 실행 끝 (매개변수 2개인 경우)


