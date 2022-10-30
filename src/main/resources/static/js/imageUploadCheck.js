console.log("이미지 upload 체크 js 실행");

const handler = {
    init() {
        const fileInput = document.getElementById('imageFiles');
        const preview = document.getElementById('preview');
        fileInput.addEventListener('change', () => {
            if(preview.childElementCount > 0) {
                while(preview.firstChild)  {
                    preview.removeChild(preview.firstChild);
                }
            }

            console.dir(fileInput)
            const files = Array.from(fileInput.files)
            files.forEach(file => {
                preview.innerHTML += `
                <p id="${file.lastModified}">
                    ${file.name}
                    <button data-index='${file.lastModified}' class='file-remove'>삭제</button>
                </p>`;
            });
        });
    },

    removeFile: () => {
        document.addEventListener('click', (e) => {
            if(e.target.className !== 'file-remove') return;
            const removeTargetId = e.target.dataset.index;
            const removeTarget = document.getElementById(removeTargetId);
            const files = document.getElementById('imageFiles').files;
            const dataTranster = new DataTransfer();

            // document.querySelector('#file-input').files =
            //             Array.from(files).filter(file => file.lastModified !== removeTarget);


            Array.from(files)
                .filter(file => file.lastModified != removeTargetId)
                .forEach(file => {
                    dataTranster.items.add(file);
                });

            document.getElementById('imageFiles').files = dataTranster.files;

            removeTarget.remove();
        })
    }
}

handler.init()
handler.removeFile()