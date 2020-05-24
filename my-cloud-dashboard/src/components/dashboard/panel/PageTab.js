import React from 'react';

class PageTab extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (

      <nav>
        <div class="nav nav-tabs" id={this.props.id}>
          {childrens}
        </div>
      </nav>

    );
  }
}

export default PageTab;