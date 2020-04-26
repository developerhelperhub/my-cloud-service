import React from 'react';

class ToolbarItem extends React.Component {


  render() {
    return (
      <li class="nav-item">
        <a class="nav-link" href="#"><i class={this.props.icon}></i></a>
      </li>
    );
  }
}

export default ToolbarItem;