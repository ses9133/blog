document.addEventListener("DOMContentLoaded", function () {

    let selectedAmount = 0;

    const $btns = document.querySelectorAll('.point-btn');
    const $displayAmount = document.getElementById('selectedAmount');
    const $chargeBtn = document.getElementById('chargeBtn');

    function updateAmount(amount) {
        selectedAmount = amount;
        $displayAmount.textContent = amount.toLocaleString();

        $chargeBtn.disabled = amount <= 0;
    }

    $btns.forEach(btn => {
        btn.addEventListener('click', function () {
            $btns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');

            const amount = parseInt(this.getAttribute('data-amount'));
            updateAmount(amount);
        });
    });

    $chargeBtn.addEventListener('click', async function () {
        try {
            console.log("## 생성전 ###");
            // 1. 결제 번호 생성
            const res = await fetch("/api/v1/payment/prepare", {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    amount: selectedAmount
                })
            });

            const data = await res.json();
            const paymentId = data.data.paymentId;

            // 2. 결제 요청 (V2 방식)
            const response = await PortOne.requestPayment({
                storeId: "store-d8111b19-87ed-4114-88b0-5372f91a9a40",
                channelKey: "channel-key-7d4b97fb-b9c3-43d0-b63c-324a55d9e2bf",
                paymentId: paymentId,
                orderName: "포인트 충전",
                totalAmount: Number(selectedAmount),
                currency: "CURRENCY_KRW",
                payMethod: "EASY_PAY"
            });

            // 3. 결제 결과 처리
            if (response.code != null) {
                console.error("## 결제 실패 코드:", response.code);
                console.error("## 결제 실패 메시지:", response.message);
                return alert("결제 실패: " + response.message);
            }

            // 4. 백엔드 검증 API 호출
            verifyPaymentAndCharge(response.paymentId);
        } catch (error) {
            alert("결제 요청 실패");
        }
    });


    function verifyPaymentAndCharge(payment_id) {
        fetch('/api/v1/payment/verify', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                paymentId: payment_id
            })
        }).then(res => {

            if (!res.ok) {
                return res.json().then((e) => {
                    throw new Error(e.message);
                });
            }
            return res.json();
        })
            .then(data => {
                alert(`${data.data.amount} 포인트 충전이 완료되었습니다.`);
                location.href = '/me';
            })
            .catch(() => {
                alert('결제 검증에 실패했습니다\n관리자에게 문의하세요');
            });
    }
});