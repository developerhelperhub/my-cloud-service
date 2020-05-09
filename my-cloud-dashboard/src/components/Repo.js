import React from 'react';

class Repo extends React.Component {

    static async fetch(url, method, headers, body) {

        let formBody = [];
        var isFormUrlEncoded = false;

        for (let head in headers) {
            if (head == "Content-Type" && headers[head].includes("x-www-form-urlencoded")) {
                isFormUrlEncoded = true;
            }
        }


        if (isFormUrlEncoded) {
            
            for (let property in body) {
                let encodedKey = encodeURIComponent(property);
                let encodedValue = encodeURIComponent(body[property]);

                formBody.push(encodedKey + "=" + encodedValue);
            }

            formBody = formBody.join("&");

        } else {
            formBody = JSON.stringify(body);
        }

        const requestOptions = {
            method: method,
            headers: headers,
            body: formBody
        };


        const response = await fetch(url, requestOptions)
            .then(response => Promise.all([response.status, response.json()]))
            .then(response => {

                const status = response[0];
                var data = response[1];
                var errorMessage = data.error_description != undefined ? data.error_description : data.error;

                const res = {
                    status: status,
                    errorMessage: errorMessage,
                    data: data
                }

                return res;
            }).catch(error => {

                const res = {
                    status: 0,
                    errorMessage: 'Server can\'t be connected!',
                    data: ''
                }

                return res
            });

        return response;
    }
}

export { Repo };