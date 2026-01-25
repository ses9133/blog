async function updateUser() {
    const updateForm = document.querySelector("#updateForm");
    const formData = new FormData(updateForm);

    try {
        const response = await fetch(`/api/v1/me`, {
            method: "PUT",
            body: formData
        });

        const result = await response.json();

        if(result.success) {
            alert("회원정보가 수정되었습니다.");
            location.href = `/me`;
        } else {
            throw new Error(result.message || "수정에 실패했습니다.");
        }
    } catch (e) {
        alert(e.message);
    }
}