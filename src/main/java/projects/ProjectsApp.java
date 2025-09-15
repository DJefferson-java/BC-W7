package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	private Scanner sc = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;

	//@formatter:off
	private List<String> operations = List.of(
			"1) Add a project", 
			"2) List Project", 
			"3} Select a project",
			"4) Update project details",
			"5) Delete a project"
			
			);
   //@formatter:on

	public static void main(String[] args) {

		new ProjectsApp().processUserSelections();
	}

	// Selections made available for the user to choose which task to complete.
	private void processUserSelections() {
		boolean done = false;

		while (!done) {
			try {

				int selection = getUserSelection();

				switch (selection) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;

				case 2:
					listProjects();
					break;

				case 3:
					selectProjects();
					break;

				case 4:
					updateProjectDetails();
					break;
				case 5:
					deleteProject();
					break;
				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
					break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e.toString() + "Try again");
			}
		}

	}

	private void deleteProject() {
		listProjects();

		Integer projectId = getIntInput("Enter the ID of the project to Delete: ");

		projectService.deleteProject(projectId);
		System.out.println("Project " + projectId + " was deleted succesfully.");

		if (Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
			curProject = null;
		}
	}

	private void updateProjectDetails() {
		if (Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project");
			return;
		}

		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		BigDecimal estimatedHours = getDecimalInput(
				"Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
		Integer difficulty = getIntInput("Enter the project difficulty [" + curProject.getDifficulty() + "]");
		String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");

		Project project = new Project();
		project.setProjectId(curProject.getProjectId());
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);

		projectService.modifyProjectDetails(project);
		curProject = projectService.fetchProjectById(curProject.getProjectId());

	}

//Returns a project selested from the DB by ID
	private void selectProjects() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");

		curProject = null;

		curProject = projectService.fetchProjectById(projectId);

	}

	// List all projects inside the DB
	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();

		System.out.println("\nProjects: ");

		projects.forEach(
				project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));

	}

	// Allows a user to add a project to the DB
	private void createProject() {
		String projectName = getStringInput("Enter the project name: ");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours: ");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours: ");
		Integer difficulty = getIntInput("Enter the project difficulty(1-5): ");
		String notes = getStringInput("Enter project notes: ");

		Project project = new Project();

		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);

	}

	// Converts string to decimal format ith 2 decimal points
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " Is not a valid decimal number. Try Again.");
		}
	}

	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput("Enter a menu selection");
		// Checks if the user input is null, if so will exit the program, if not will
		// continue with the input
		return Objects.isNull(input) ? -1 : input;
	}

	// Converts string input to an integer
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " Is not a valid number. Try Again.");
		}
	}

	// Trims the input if it is not null, if null exits the program
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = sc.nextLine();
		return input.isBlank() ? null : input.trim();
	}

	// print results to the screen
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit: ");
		operations.forEach(line -> System.out.println("    " + line));

		if (Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}

	}

	// exits the menu
	private boolean exitMenu() {
		System.out.println("\n Exiting the menu. TTFN!");
		return true;
	}

}
