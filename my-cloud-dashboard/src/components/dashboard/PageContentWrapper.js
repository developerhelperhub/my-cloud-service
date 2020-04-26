import React from 'react';

class PageContentWrapper extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (

      <div class="page-content-wrapper">

        {childrens}

      </div>

    );
  }
}

export default PageContentWrapper;