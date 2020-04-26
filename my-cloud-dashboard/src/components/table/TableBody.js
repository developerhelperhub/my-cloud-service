import React from 'react';

class TableBody extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (

      <tbody>

        {childrens}

      </tbody>

    );
  }
}

export default TableBody;