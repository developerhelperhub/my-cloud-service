import React from 'react';

class PageTabContent extends React.Component {

    render() {
        const childrens = React.Children.map(this.props.children, children =>
            React.cloneElement(children)
        );

        return (
            <div class="tab-content detail" id={this.props.id}>
                {childrens}
            </div>
        );
    }
}

export default PageTabContent;