import React from 'react';

class ToolbarDropdownContainer extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (

      <div class="container">
        <div class="content">

          {childrens}

        </div>
      </div>
    );
  }
}

export default ToolbarDropdownContainer;