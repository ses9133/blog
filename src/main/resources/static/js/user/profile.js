document.addEventListener("DOMContentLoaded", () => {
    const btnDeleteProfile = document.getElementById("btnDeleteProfile");
    if(!btnDeleteProfile) return;

    btnDeleteProfile.addEventListener('click', async function () {
        if(!confirm("프로필을 삭제하시겠습니까 ?")) return;

        try {
            const response = await fetch(`/api/v1/me/profile-image`, {
                method: 'DELETE'
            });
            if(!response.ok) {
                throw new Error('프로필 삭제에 실패했습니다.');
            } else {
                alert("프로필이 삭제되었습니다.");
                const imageContainer = document.getElementById("imageContainer");
                imageContainer.innerHTML = `
                   <div class="rounded-circle bg-secondary d-inline-flex align-items-center justify-content-center border"
                         style="width: 150px; height: 150px;">
                        <span class="text-white fs-5">프로필 사진 없음</span>
                    </div>
               `;
            }
        } catch (error) {
            alert(error.message);
        }
    });
});