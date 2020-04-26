import React from 'react';
import PropTypes from 'prop-types';

class TableTh extends React.Component {


  render() {

    var childrens ;
    
    if (this.props.children.type == PropTypes.object) {

      childrens = React.Children.map(this.props.children, children =>
        React.cloneElement(children)
      );

    } else {
      childrens = this.props.children;
    }
    
    return (

      <th width={this.props.width}>
        {childrens}
      </th>

    );
  }
}

export default TableTh;