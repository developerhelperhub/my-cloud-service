import React from 'react';

class PageTabHead extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    var selected = false;
    if (this.props.selected != null) {
      selected = this.props.selected;
    }

    var classNavItemLink = "nav-item nav-link";
    if (this.props.active != null && this.props.active) {
      classNavItemLink = classNavItemLink + " active";
    }
    

    return (
      <a class={classNavItemLink} id={this.props.id} data-toggle="tab" href={this.props.href} role="tab" aria-controls={this.props.controls} aria-selected={selected}>
        {this.props.title}
      </a>
    );
  }
}

export default PageTabHead;