import React from 'react';
import { Redirect } from 'react-router-dom';
import AppApiRepo from '../../../common/AppApiRepo';


import './Login.css'
import '../Registration.css'

class Login extends React.Component {

  constructor(props) {
    super(props)

    this.state = {
      isAuthenticated: false,
      isError: false,
      errorMessage: ''
    }

    this.handleSubmit = this.handleSubmit.bind(this);

    this.username = React.createRef();
    this.password = React.createRef();
  }

  async handleSubmit(event) {
    event.preventDefault();

    const response = await AppApiRepo.fetch('/oauth/token', 'POST', {
      'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
      'Authorization': AppApiRepo.getBasicToken(),
    }, {
      'grant_type': 'password',
      'username': this.username.current.value,
      'password': this.password.current.value
    })

    var isAuthenticated = false;
    var errorMessage = '';
    var isError = false;

    if (response.status == 200) {

      isAuthenticated = true;
      localStorage.setItem('authentiction', JSON.stringify(response.data));

    } else {
      isError = true;
      errorMessage = response.errorMessage;
    }

    this.setState({
      isAuthenticated: isAuthenticated,
      isError: isError,
      errorMessage: errorMessage,
    });

    if (isAuthenticated) {
      window.location.reload(true);
    }
  }

  render() {

    var errorMessage = '';

    if (this.state.isError) {
      errorMessage = <div class="error"> {this.state.errorMessage} </div>
    }

    if (this.state.isAuthenticated) {

      return <Redirect to="/" />

    } else {

      return (
        < div class="card" >

          <div class="card-header">
            <h3>Sign In</h3>
          </div>

          <div class="card-body">

            <form onSubmit={(e) => this.handleSubmit(e)}>

              <div class="input-group form-group">
                <div class="input-group-prepend">
                  <span class="input-group-text"><i class="fas fa-user"></i></span>
                </div>
                <input type="text" ref={this.username} name="username" class="form-control" placeholder="username" />
              </div>

              <div class="input-group form-group">
                <div class="input-group-prepend">
                  <span class="input-group-text"><i class="fas fa-key"></i></span>
                </div>
                <input type="password" ref={this.password} name="password" class="form-control" placeholder="password" />
              </div>

              {errorMessage}

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
        </div >

      );
    }
  }
}

export default Login;