import React from 'react';

class ToolbarIcon extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );
    
    var styleClass = "navbar-nav " + this.props.align;

    return (

      <ul class={styleClass}>
        
        {childrens}
        
      </ul>
      
    );
  }
}

export default ToolbarIcon;