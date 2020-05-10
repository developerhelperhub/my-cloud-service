import React from 'react';

class PagePanelHead extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (
      <div class="header">
        <div class="title">{this.props.title}</div>
        {childrens}
      </div>
    );
  }
}

export default PagePanelHead;