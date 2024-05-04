
function createNewTask(e) {
  e.preventDefault();

  let data = {};
  let fields = e.target.elements;
  $.each(fields, function (i, field) {
    if (field.value) data[field.name] = field.value;
  });
  const csrfToken = document.querySelector('meta[name="XSRF-TOKEN"]').getAttribute('content');

  $.ajax({
    url: "/api/epic",
    type: "POST",
    dataType: "json",
    headers: { "X-XSRF-TOKEN": csrfToken},
    contentType: "application/json",
    data: JSON.stringify(data),
    success: function (res) {
      console.log(res);
      const card = document.createElement('div');
      card.className = 'card task-box mb-3';
    
      const cardBody = document.createElement('div');
      cardBody.className = 'card-body';
    
      const dropdown = document.createElement('div');
      dropdown.className = 'dropdown float-end';
    
      const dropdownToggle = document.createElement('a');
      dropdownToggle.className = 'dropdown-toggle arrow-none';
      dropdownToggle.setAttribute('data-bs-toggle', 'dropdown');
      dropdownToggle.setAttribute('aria-expanded', 'false');
      dropdownToggle.innerHTML = '<i class="mdi mdi-dots-vertical m-0 text-muted h5"></i>';
    
      const dropdownMenu = document.createElement('div');
      dropdownMenu.className = 'dropdown-menu dropdown-menu-end';
    
      const editLink = document.createElement('a');
      editLink.className = 'dropdown-item edittask-details';
      editLink.setAttribute('data-bs-toggle', 'modal');
      editLink.setAttribute('data-bs-target', '#editTaskModal');
      editLink.textContent = 'Edit';
      editLink.onclick = function () { populateModal(this, res.epicID); };
    
      const deleteLink = document.createElement('a');
      deleteLink.className = 'dropdown-item deletetask';
      deleteLink.textContent = 'Delete';
      deleteLink.onclick = function () { deleteTask(this, res.epicID); };
    
      dropdownMenu.appendChild(editLink);
      dropdownMenu.appendChild(deleteLink);
    
      dropdown.appendChild(dropdownToggle);
      dropdown.appendChild(dropdownMenu);
    
      const item = document.createElement('div');
      item.id = `item-${res.epicID}`;
    
      const title = document.createElement('h6');
      title.className = 'font-size-15 text-dark';
      title.textContent = data.title;
    
      const date = document.createElement('span');
      date.className = 'text-muted mb-2';
      date.textContent = new Date().toDateString();
    
      const team = document.createElement('span');
      team.className = 'text-muted mb-2 float-sm-end';
      team.textContent = data.team;
    
      const description = document.createElement('p');
      description.textContent = data.description;
    
      item.appendChild(title);
      item.appendChild(date);
      item.appendChild(team);
      item.appendChild(description);
    
      cardBody.appendChild(dropdown);
      cardBody.appendChild(item);
      card.appendChild(cardBody);
    
      document.getElementById('upcoming-task').appendChild(card);
    
      // Assuming the modal hide method is necessary, ensure it's handled securely
      $("#taskModal").modal("hide");
    },
    error: function (err) {
      console.error(err);
    },
  });
}

function populateModal(e, id) {
  let fields = e.parentElement.parentElement.nextElementSibling.children;
  $("#etaskid").val(id);
  $("#etitle").val($.trim(fields[0].innerText));
  $("#eteam").val($.trim(fields[2].innerText));
  $("#edescription").val($.trim(fields[3].innerText));
}

function updateTask(e) {
  e.preventDefault();

  let data = {};
  let fields = e.target.elements;
  $.each(fields, function (i, field) {
    if (field.value) data[field.name] = field.value;
  });
  const csrfToken = document.querySelector('meta[name="XSRF-TOKEN"]').getAttribute('content');
  $.ajax({
    url: `/api/epic/${data.id}`,
    type: "PUT",
    dataType: "json",
    headers: { "X-XSRF-TOKEN": csrfToken},
    contentType: "application/json",
    data: JSON.stringify(data),
    success: function (res) {
      console.log(res);
      let elements = $("#item-" + data.id)[0].children;
      elements[0].innerText = $("#etitle").val();
      elements[2].innerText = $("#eteam").val();
      elements[3].innerText = $("#edescription").val();
      $("#editTaskModal").modal("hide");
    },
    error: function (err) {
      console.error(err);
    },
  });
}

function updateTaskStatus(e) {
  if (e[0] != "drop") return;
  let ele = e[1];
  let target = e[2];
  let id = ele.children[0].children[1].id.substring(5);
  let status = "BACKLOG";
  if (target.id.startsWith("inprogress")) status = "IN_PROGRESS";
  else if (target.id.startsWith("complete")) status = "COMPLETED";

  $.ajax({
    url: `/api/epic/${id}/${status}`,
    type: "PUT",
    dataType: "json",
    success: function (res) {
      console.log(res);
    },
    error: function (err) {
      console.error(err);
    },
  });
}

function deleteTask(e, id) {
  $.ajax({
    url: `/api/epic/${id}`,
    type: "DELETE",
    dataType: "json",
    success: function (res) {
      console.log(res);
      e.parentElement.parentElement.parentElement.parentElement.remove();
    },
    error: function (err) {
      console.error(err);
    },
  });
}

$("#newtask")[0]?.addEventListener("submit", createNewTask);
$("#edittask")[0].addEventListener("submit", updateTask);
