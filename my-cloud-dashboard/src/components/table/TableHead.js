import React from 'react';

class TableHead extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (

      <thead>

        {childrens}

      </thead>

    );
  }
}

export default TableHead;