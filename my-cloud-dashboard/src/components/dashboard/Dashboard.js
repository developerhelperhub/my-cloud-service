import React from 'react';
import {
  BrowserRouter as Router
} from "react-router-dom";


import './Dashboard.css'

class Dashboard extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (
        <div class="d-flex chiller-theme" id="wrapper">

          {childrens}

        </div>
    );
  }
}

export default Dashboard;