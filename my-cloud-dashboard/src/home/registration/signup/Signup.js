import React from 'react';

import './Signup.css'
import '../Registration.css'

class Signup extends React.Component {
  render() {
    return (
      <div class="card">

        <div class="card-header">
          <h3>Sign Up</h3>
        </div>

        <div class="card-body">
          <form>
            <div class="input-group form-group">
              <div class="input-group-prepend">
                <span class="input-group-text"><i class="fas fa-email"></i></span>
              </div>
              <input type="text" class="form-control" placeholder="email" />
            </div>

            <div class="input-group form-group">
              <div class="input-group-prepend">
                <span class="input-group-text"><i class="fas fa-user"></i></span>
              </div>
              <input type="password" class="form-control" placeholder="username" />
            </div>

            <div class="input-group form-group">
              <div class="input-group-prepend">
                <span class="input-group-text"><i class="fas fa-key"></i></span>
              </div>
              <input type="password" class="form-control" placeholder="password" />
            </div>

            <div class="input-group form-group">
              <div class="input-group-prepend">
                <span class="input-group-text"><i class="fas fa-key"></i></span>
              </div>
              <input type="password" class="form-control" placeholder="confirm" />
            </div>

            <div class="form-group">
              <input type="submit" value="Submit" class="btn float-right login_btn" />
            </div>

          </form>
        </div>

        <div class="card-footer">
          <div class="d-flex justify-content-center links">
            Have an account?<a href="#">Sign In</a>
          </div>
          <div class="d-flex justify-content-center">
            <a href="#">Forgot your password?</a>
          </div>
        </div>
      </div>
    );
  }
}

export default Signup;