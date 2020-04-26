import React from 'react';

class PageContent extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (

      <div class="page-content container-fluid">

        {childrens}

      </div>

    );
  }
}

export default PageContent;