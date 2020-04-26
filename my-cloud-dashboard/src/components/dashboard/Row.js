import React from 'react';

class Row extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (

      <div class="row">

        {childrens}

      </div>

    );
  }
}

export default Row;