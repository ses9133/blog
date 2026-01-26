document.addEventListener("DOMContentLoaded", () => {
    const $boardDeleteBtn = document.getElementById("deleteBtn");

    if ($boardDeleteBtn) {
        $boardDeleteBtn.addEventListener("click", async () => {
            const boardId = $boardDeleteBtn.dataset.boardId;
            const deleteConfirm = confirm("게시글을 삭제하시겠습니까?");

            if (!deleteConfirm) {
                return;
            }

            try {
                const response = await fetch(`/api/v1/boards/${boardId}`, {
                    method: "DELETE"
                });
                if (!response.ok) {
                    throw new Error("삭제에 실패하였습니다.");
                }
                alert("삭제 완료되었습니다.")
                location.href = `/boards`;
            } catch (e) {
                alert(e.message);
            }
        });
    }

    const $replyDeleteBtn = document.getElementById("replyDeleteBtn");

    if ($replyDeleteBtn) {
        $replyDeleteBtn.addEventListener("click", async () => {
            const replyId = $replyDeleteBtn.dataset.replyId;

            const deleteConfirm = confirm("댓글을 삭제하시겠습니까?");

            if (!deleteConfirm) {
                return;
            }

            try {
                const response = await fetch(`/api/v1/replies/${replyId}`, {
                    method: "DELETE"
                });
                if (!response.ok) {
                    throw new Error("댓글 삭제에 실패하였습니다.");
                }
                alert("댓글이 삭제되었습니다.");
                location.reload();
            } catch (e) {
                alert(e.message);
            }
        });
    }
});