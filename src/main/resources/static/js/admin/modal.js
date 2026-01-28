function openRefundModal(id, username, reason, amount, statusDisplay) {

    document.getElementById("modalRefundId").value = id;
    document.getElementById("modalUsername").textContent = username;
    document.getElementById("modalReason").textContent = reason ? reason : "(사유없음)";
    document.getElementById("modalAmount").textContent = parseInt(amount).toLocaleString() + ' P';

    const footer = document.getElementById("modalFooter");
    const rejectDiv = document.getElementById("rejectReasonDiv");
    rejectDiv.style.display = 'none';
    document.getElementById("rejectReasonInput").value = "";

    if(statusDisplay === '대기중') {
        footer.innerHTML = `
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                <button type="button" class="btn btn-danger" onclick="toggleRejectInput()">거절</button>
                <button type="button" class="btn btn-primary" onclick="approveRefund(${id})"> 환불 승인</button>
            `;
    } else {
        footer.innerHTML = `
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                `;
    }
}

function toggleRejectInput() {
    const $rejectDiv = document.getElementById("rejectReasonDiv");
    const $footer = document.getElementById("modalFooter");
    const id = document.getElementById("modalRefundId").value;

    $rejectDiv.style.display = 'block';
    $footer.innerHTML = `
             <button type="button" class="btn btn-secondary" onclick="hideRejectInput()">취소</button>
             <button type="button" class="btn btn-danger" onclick="rejectRefund(${id})">거절 확정</button>
        `;
}

function rejectRefund(id) {
    const rejectReason = document.getElementById("rejectReasonInput").value.trim();

    if(!rejectReason) {
        alert("거절 사유를 입력해주세요");
        return;
    }

    if(!confirm("환불 요청을 정말로 거절하시겠습니까?")) {
        return;
    }

    const $form = document.createElement('form');
    $form.method = 'POST';
    $form.action = `/admin/refunds/${id}/reject`;

    const $input = document.createElement('input');
    $input.type = "hidden";
    $input.name = "rejectReason";
    $input.value = rejectReason;

    $form.appendChild($input);

    document.body.appendChild($form);

    $form.submit();

}

function hideRejectInput() {
    const id = document.getElementById("modalRefundId").value;
    const $rejectDiv = document.getElementById("rejectReasonDiv");
    const $footer = document.getElementById("modalFooter");

    $rejectDiv.style.display = 'none';
    document.getElementById("rejectReasonInput").value = "";

    $footer.innerHTML = `
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                <button type="button" class="btn btn-danger" onclick="toggleRejectInput()">거절</button>
                <button type="button" class="btn btn-primary" onclick="approveRefund(${id})">환불 승인</button>
            `;
}

function approveRefund(id) {
    if(!confirm("환불을 승인하시겠습니까?\n(즉시 환불처리됩니다.)")) {
        return;
    }

    const $form = document.createElement("form");
    $form.method = "POST";
    $form.action = `/admin/refunds/${id}/approve`;

    document.body.appendChild($form);
    $form.submit();
}