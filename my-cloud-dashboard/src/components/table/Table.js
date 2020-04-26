import React from 'react';

class Table extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (

      <table class="table table-bordered table-striped">

        {childrens}

      </table>

    );
  }
}

export default Table;