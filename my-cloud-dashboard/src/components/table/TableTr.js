import React from 'react';

class TableTr extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (

      <tr>

        {childrens}

      </tr>

    );
  }
}

export default TableTr;