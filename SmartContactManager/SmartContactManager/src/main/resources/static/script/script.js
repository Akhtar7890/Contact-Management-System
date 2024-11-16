

const toggleSidebar = () => {
    const sidebar = $(".sidebar");
    const content = $(".content");

    if (sidebar.is(":visible")) {
        sidebar.fadeOut(300); // Use fadeOut for a smoother transition
        content.animate({ marginLeft: "0%" }, 300); // Animate the margin change
    } else {
        sidebar.fadeIn(300); // Use fadeIn for a smoother transition
        content.animate({ marginLeft: "20%" }, 300); // Animate the margin change
    }
};

const search=()=>{
//    console.log("called searching");

        let query=$("#search-input").val();
        console.log(query);

        if(query==''){
            $(".search-result").hide();
        }else{
            console.log(query);
            let url=`http://localhost:8080/search/${query}`;

            fetch(url).then((response) =>{
                return response.json();
            })
            .then((data)=>{
                let text=`<div class='list-group'>`

                data.forEach((contact)=>{
                    text +=`<a href="/user/contact/${contact.c_id}" class="list-group-item list-group-item-action">${contact.name}</a>`
                })

                text +=`</div>`

                $(".search-result").html(text);
                $(".search-result").show();
            })
            $(".search-result").show();
        }

};