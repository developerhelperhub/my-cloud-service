import React from 'react';

import "./PageTabPanel.css"

class PageTabPanel extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    var panelClass = "page-panel " + this.props.cols;

    return (
      <div class={panelClass}>
        <div class="body">
          {childrens}
        </div>
      </div>

    );
  }
}

export default PageTabPanel;