import React from 'react';

class PagePanel extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    var panelClass = "page-panel " + this.props.cols;
    var bodyHeight = "100%";

    if (this.props.height != null) {
      bodyHeight = this.props.height.value + this.props.height.unit;
    }

    var detailHeight;

    if (this.props.height != null) {
      detailHeight =  this.props.height.value - 50;
      detailHeight = detailHeight + this.props.height.unit;
    } else {
      detailHeight = "100%";
    }

    return (
      <div class={panelClass}>

        <div class="body" style={{ height: bodyHeight }}>
          <div class="title">{this.props.title}</div>
          <div class="detail" style={{ height: detailHeight }} id={this.props.id}>
            {childrens}
          </div>
        </div>

      </div>

    );
  }
}

export default PagePanel;