import React from 'react';

class PageTabPane extends React.Component {

    render() {
        const childrens = React.Children.map(this.props.children, children =>
            React.cloneElement(children)
        );

        var showOrActive = "";

        if (this.props.show != undefined && this.props.show) {
            showOrActive = "show";
        }

        if (this.props.active != undefined && this.props.active) {
            showOrActive = showOrActive + " " + "active";
        }

        var classTabPane = "tab-pane fade " + showOrActive;
        return (
            <div class={classTabPane} id={this.props.id} role="tabpanel" aria-labelledby={this.props.labelledby}>
                {childrens}
            </div>
        );
    }
}

export default PageTabPane;