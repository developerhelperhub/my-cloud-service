import React from 'react';

class ToolbarDropdownFooter extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (

      <div class="footer size">
        <a class="body">{this.props.label}</a>
        
        {childrens}

      </div>
    );
  }
}

export default ToolbarDropdownFooter;