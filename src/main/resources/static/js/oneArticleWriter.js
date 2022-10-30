// 사용자가 올리는 사진의 갯수 가져오기
const picture = document.querySelectorAll('.picture')
console.log(picture.length)

// 버튼에 이벤트 할당
const leftArrow = document.getElementById("leftArrow")
const rightArrow = document.getElementById("rightArrow")

// div 크기 계산을 위해 가져오는 값
const pictureArea = document.querySelector('.item-picture-area')
const itemPictures = document.querySelector('.item-pictures')


// 슬라이드 버튼 값 가져오기
const btn1 = document.querySelector('.btn1')
const btn2 = document.querySelector('.btn2')
const btn3 = document.querySelector('.btn3')

// 인덱스 값 초기화 (화면에 보여지는 사진의 인덱스)
let currentIndex = 0

// 왼쪽 버튼을 클릭하면 인덱스가 1 감소
leftArrow.addEventListener("click", () => {
    currentIndex--;
    currentIndex = currentIndex < 0 ? 0 : currentIndex;
    itemPictures.style.marginLeft = `-${pictureArea.clientWidth * currentIndex}px`
    
    currentIndexReturn()
})

// 오른쪽 버튼을 클릭하면 인덱스가 1 증가
rightArrow.addEventListener("click", () => {
    currentIndex++;
    currentIndex = currentIndex >= picture.length ? picture.length - 1 : currentIndex;
    itemPictures.style.marginLeft = `-${pictureArea.clientWidth * currentIndex}px`

    currentIndexReturn()
})

//버튼 누를 시 인덱스에 맞는 사진 출력
btn1.addEventListener("click", () => {
    currentIndex = 0
    itemPictures.style.marginLeft = `-${pictureArea.clientWidth * currentIndex}px`
    
    currentIndexReturn()
})

btn2.addEventListener("click", () => {
    currentIndex = 1
    itemPictures.style.marginLeft = `-${pictureArea.clientWidth * currentIndex}px`
  
    currentIndexReturn()
})

btn3.addEventListener("click", () => {
    currentIndex = 2
    itemPictures.style.marginLeft = `-${pictureArea.clientWidth * currentIndex}px`
    
    currentIndexReturn()
})

const currentIndexReturn = () => {
    if(currentIndex == 0) {
        btn1.style.backgroundColor = 'gray'
        btn2.style.backgroundColor = 'lightgray'
        btn3.style.backgroundColor = 'lightgray'
    } else if(currentIndex == 1) {
        btn1.style.backgroundColor = 'lightgray'
        btn2.style.backgroundColor = 'gray'
        btn3.style.backgroundColor = 'lightgray'
    } else {
        btn1.style.backgroundColor = 'lightgray'
        btn2.style.backgroundColor = 'lightgray'
        btn3.style.backgroundColor = 'gray'
    }
}

currentIndexReturn()