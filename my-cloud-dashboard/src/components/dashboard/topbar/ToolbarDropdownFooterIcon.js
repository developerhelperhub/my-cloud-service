import React from 'react';

class ToolbarDropdownFooterIcon extends React.Component {


  render() {
   
    return (
      <a class="button">
        <i class={this.props.icon}></i>
      </a>
    );
  }
}

export default ToolbarDropdownFooterIcon;