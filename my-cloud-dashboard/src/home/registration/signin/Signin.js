import React from 'react';

import './Signin.css'
import '../Registration.css'

class Signin extends React.Component {

  handleSubmit = async function (event) {
    event.preventDefault();

    let details = {
      'grant_type': 'password',
      'username': 'mycloud',
      'password': 'mycloud@1234'
    };

    let formBody = [];
    for (let property in details) {
      let encodedKey = encodeURIComponent(property);
      let encodedValue = encodeURIComponent(details[property]);
      formBody.push(encodedKey + "=" + encodedValue);
    }
    formBody = formBody.join("&");

    // Simple POST request with a JSON body using fetch
    const requestOptions = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        'Authorization': 'Basic bXktY2xvdWQtaWRlbnRpdHk6VmtacHp6S2EzdU1xNHZxZw==',
      },
      body: formBody
    };

    const data = await fetch('http://localhost:8085/identity/oauth/token', requestOptions)
      .then(response => response.json())
      .then(data => {
        return data;
      });

      console.log(data.access_token);
      console.log(data.token_type);
      console.log(data.refresh_token);
      console.log(data.expires_in);
  }

  render() {

    return (
      <div class="card">

        <div class="card-header">
          <h3>Sign In</h3>
        </div>

        <div class="card-body">
          <form onSubmit={this.handleSubmit}>
            <div class="input-group form-group">
              <div class="input-group-prepend">
                <span class="input-group-text"><i class="fas fa-user"></i></span>
              </div>
              <input type="text" class="form-control" placeholder="username" />
            </div>

            <div class="input-group form-group">
              <div class="input-group-prepend">
                <span class="input-group-text"><i class="fas fa-key"></i></span>
              </div>
              <input type="password" class="form-control" placeholder="password" />
            </div>

            <div class="row align-items-center remember">
              <input type="checkbox" />Remember Me
                </div>

            <div class="form-group">
              <input type="submit" value="Login" class="btn float-right login_btn" />
            </div>

          </form>
        </div>

        <div class="card-footer">
          <div class="d-flex justify-content-center links">
            Don't have an account?<a href="#">Sign Up</a>
          </div>
          <div class="d-flex justify-content-center">
            <a href="#">Forgot your password?</a>
          </div>
        </div>
      </div>

    );
  }
}

export default Signin;