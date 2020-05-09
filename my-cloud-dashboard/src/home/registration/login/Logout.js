import React from 'react';
import { Redirect } from 'react-router-dom';

class Logout extends React.Component {

  render() {

    localStorage.removeItem('authentiction');

    console.log("Logout.....")

    return (
      <Redirect to='/login'/>
    );
  }
}

export default Logout;