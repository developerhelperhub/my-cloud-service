import React from 'react';

class Topbar extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (
      <nav class="navbar sticky-top navbar-expand-lg navbar-light topbar border-bottom">
        <a class="nav-link" id="menu-toggle" href="#"><i class="fa fa-arrow-left"></i></a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarTopbar" aria-controls="navbarTopbar" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarTopbar">

          {childrens}

        </div>

      </nav>

    );
  }
}

export default Topbar;