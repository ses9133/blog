async function callSendApi() {
    let email = document.querySelector("#email").value;

    if(!email) {
        alert("이메일을 입력해주세요");
        return;
    }
    try {
        const response = await fetch("/api/v1/email/send", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({email: email})
        });

        if(response.ok) {
            alert("인증번호가 메일로 전송 되었습니다. 메일을 확인해주세요");
            document.querySelector("#code-box").style.display = "block";
        } else {
            const error = response.json();
            alert("이메일 전송 실패\n" + error.message);
        }
    } catch (e) {
        alert("서버 통신 오류 " + e.message);
    }
}

async function callVerifyApi() {
    let email = document.querySelector("#email").value;
    let code = document.querySelector("#code").value;

    if(!code) {
        alert("인증번호를 입력해주세요");
        return;
    }

    try {
        const response = await fetch("/api/v1/email/verify", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email:email,
                code: code
            })
        });

        const msgBox = document.querySelector("#msg");

        if(response.ok) {
            msgBox.innerHTML = "<span style='color: green' >인증되었습니다</span>"
            document.querySelector("#isEmailVerified").value = true;
            document.querySelector("#email").readOnly = true;
        } else {
            msgBox.innerHTML = "<span style='color: red' >인증번호가 틀렸습니다</span>"
            document.querySelector("#isEmailVerified").value = false;
        }
    } catch (e) {
        console.log(e);
        alert("네트워크 오류 발생");
    }
}
