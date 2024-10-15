package com.crossly.list_app.view;

import com.crossly.list_app.entity.ListItem;
import com.crossly.list_app.repository.ListItemRepository;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;


@Route("")
@PageTitle("Global Listicle App")
public class ListView extends VerticalLayout {

	private final ListItemRepository listItemRepository;

	private final Binder<ListItem> listItemBinder;

	private final Grid<ListItem> listItemGrid;

	private static final String MAX_WIDTH = "768px";

	public ListView(ListItemRepository repo) {
		listItemRepository = repo;
		// Setting up the binder
		listItemBinder = new Binder<>(ListItem.class);
		listItemBinder.setBean(new ListItem());
		// Setting up the title input
		var titleField = new TextField();
		titleField.setPlaceholder("Title");
		titleField.setRequired(true);
		titleField.setWidthFull();
		listItemBinder.bind(titleField, ListItem::getTitle, ListItem::setTitle);
		// Setting up the description text area
		var descArea = new TextArea();
		descArea.setPlaceholder("Description");
		descArea.setWidthFull();
		listItemBinder.bind(descArea, ListItem::getDescription, ListItem::setDescription);
		// Setting up the submit button
		var submitButton = new Button("Create", event -> {
			try {
				var li = new ListItem();
				li.setTimeCreated(LocalDateTime.now());
				listItemBinder.writeBean(li);
				if (!li.getTitle().isBlank()) {
					listItemRepository.save(li);
				} else {
					Notification.show("Title cannot be blank", 5000, Notification.Position.TOP_START);
				}
				listItemBinder.setBean(new ListItem());
				renderList();
			} catch (ValidationException e) {
				notifyValidationException(e);
			}
		});
		submitButton.addClickShortcut(Key.ENTER, KeyModifier.CONTROL);
		submitButton.addThemeVariants(
				ButtonVariant.LUMO_PRIMARY,
				ButtonVariant.LUMO_SUCCESS
		);
		// Creating the form
		var form = new FormLayout();
		form.setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1)
		);
		form.addFormItem(titleField,"Title");
		form.addFormItem(descArea, "Description");
		form.add(submitButton);
		form.setMaxWidth(MAX_WIDTH);
		form.setWidthFull();
		// Setting up the table(grid)
		listItemGrid = new Grid<>(ListItem.class, false);
		listItemGrid.addColumn(new ComponentRenderer<>(this::listItemRenderer))
				.setHeader("Task")
				.setSortable(true)
				.setComparator(ListItem::getTitle);
		listItemGrid.addColumn(ListItem::getDescription)
				.setAutoWidth(true)
				.setHeader("Description");
		listItemGrid.addColumn(new LocalDateTimeRenderer<>(ListItem::getTimeCreated, "h:mm a - dd/MMM/yyyy"))
				.setHeader("Date Created")
				.setSortable(true)
				.setComparator(ListItem::getTimeCreated);

		listItemGrid.setItems(listItemRepository.findAll());
		listItemGrid.setAllRowsVisible(true);
		listItemGrid.setPageSize(10);
		// Setting up the page layout
		setAlignSelf(Alignment.CENTER);
		setMaxWidth(MAX_WIDTH);
		setSizeFull();
		add(listItemGrid, form);
		expand(listItemGrid);
	}

	private void notifyValidationException(ValidationException e) {
		for (var error : e.getValidationErrors()) {
			var notification = Notification.show(error.getErrorMessage(), 5000, Notification.Position.TOP_START);
			notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
			error.getErrorLevel().ifPresentOrElse(
					errorLevel -> {
						switch (errorLevel) {
							case INFO -> notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
							case WARNING -> notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
							default -> notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
						}
					},
					() -> notification.addThemeVariants(NotificationVariant.LUMO_ERROR)
			);
		}
	}

	private Section listItemRenderer(ListItem li) {
		var section = new Section();
		var title = li.getTitle();
		var checkBox = new Checkbox(li.getCompleted());
		checkBox.addValueChangeListener(
				event -> {
					li.setCompleted(event.getValue());
					listItemRepository.save(li);
					renderList();
				}
		);
		section.add(checkBox, new Span(title));
		return section;
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		renderList();
	}

	private void renderList() {
		var data = listItemRepository.findAll(Sort.by("timeCreated"));
		listItemGrid.setItems(data);
	}

}
