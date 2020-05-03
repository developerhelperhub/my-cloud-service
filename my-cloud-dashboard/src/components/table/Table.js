import React from 'react';

class Table extends React.Component {

  constructor(props) {
    super(props);
  }

  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    var tableWidth = "100%";
    if (this.props.width != null) {
      tableWidth = this.props.width;
    }

    return (

      <table id={this.props.id} class="table table-bordered table-striped" style={{ width: tableWidth }} >

        {childrens}

      </table>

    );
  }
}

export default Table;