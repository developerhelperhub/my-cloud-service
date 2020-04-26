import React from 'react';

class ToolbarDropdownHeader extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    var badge = "";

    if (this.props.badge != null) {
      badge = <span class={"badge list badge-pill " + this.props.badge.type}>{this.props.badge.label}</span>;
    }

    return (

      <div class="header">
        <h5 class="title">{this.props.title}</h5>

        {badge}

      </div>
    );
  }
}

export default ToolbarDropdownHeader;