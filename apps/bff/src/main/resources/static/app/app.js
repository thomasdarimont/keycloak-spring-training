let spa = {};

function callApi(url, requestOptions, onError) {
    let csrfToken = qs("meta[name=_csrf]").content;
    let csrfTokenHeader = qs("meta[name=_csrf_header]").content;
    let requestData = {
        timeout: 2000,
        method: "GET",
        credentials: "include",
        headers: {
            "Accept": "application/json",
            'Content-Type': 'application/json',
            [`${csrfTokenHeader}`]: csrfToken
        }
        , ...requestOptions
    }
    return fetch(url, requestData).catch(onError);
}


(async function onInit() {
    try {
        let targetUrl = ( //
            "/bff/api/users/me/userinfo" //
            // "/bff/api/users/me/claims" //
            // "/bff/api/users/me/remote" //

        );
        let userInfoResponse = await callApi(targetUrl, {});
        if (userInfoResponse.ok) {
            let userInfo = await userInfoResponse.json();
            console.table(userInfo);
            spa.userInfo = userInfo;
        }
    } catch (error) {
        console.log("failed to fetch userinfo: " + error);
    }

    if (spa.userInfo) {
        qs("#userInfo").innerText = JSON.stringify(spa.userInfo, null, "  ");
        qs("#login").remove()
    } else {
        qs("#logout").remove()
    }
}());