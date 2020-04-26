import React from 'react';
import PropTypes from 'prop-types';

class TableTd extends React.Component {


  render() {

    var childrens;

    if (this.props.children.type == PropTypes.object) {

      childrens = React.Children.map(this.props.children, children =>
        React.cloneElement(children)
      );

    } else {
      childrens = this.props.children;
    }

    if (this.props.value != null) {

      if (this.props.value.type == "strong") {

        childrens = <strong> {childrens} </strong>

      } else if (this.props.value.type == "label") {
        
        var label = "label " + this.props.value.label;

        childrens = <span class={label}>{childrens}</span>;

      }
    }

    return (


      <td>

        {childrens}

      </td>

    );
  }
}

export default TableTd;