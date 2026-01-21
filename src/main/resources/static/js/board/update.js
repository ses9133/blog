document.addEventListener("DOMContentLoaded", () => {
   const editBtn = document.getElementById("editBtn");

   editBtn.addEventListener("click", async () => {
       const id = editBtn.dataset.boardId;
       const data = {
           title: document.querySelector("[name=title]").value,
           content: document.querySelector("[name=content]").value,
           premium: document.getElementById("premium").checked
       };

       try {
           const response = await fetch(`/api/v1/boards/${id}`, {
               method: "PUT",
               headers: {
                   "Content-Type": "application/json"
               },
               body: JSON.stringify(data)
           });

           if(!response.ok) {
               throw new Error("수정에 실패하였습니다.");
           }
           alert("수정 완료");
           location.href = `/boards/${id}`;

       } catch (e) {
           alert(e.message);
       }
   });

});
