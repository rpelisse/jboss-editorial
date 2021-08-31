package org.jboss.editorial;

import java.net.URL;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class RosterLoader {

	public RosterLoader(URL urlToRoster) {
		rosterFile = urlToRoster;
	}

	private URL rosterFile;

    private static final String REGEX_AUTHORS = "^([a-z][a-z][a-z]) = (.*)$";
    private static final String REGEX_EDITORIALS = "^([0-9][0-9]) ([a-z][a-z][a-z])";

    private static final String AUTHOR_SEPARATOR = " = ";
    private static final String EDITORIAL_SEPARATOR = " ";

	public List<Author> loadAuthorsFromRoster() {
		return this.loadInformationsFromRoster(REGEX_AUTHORS, RosterLoader::authorFromLine);
	}

	public List<Editorial> loadEditorialsFromRoster() {
		return this.loadInformationsFromRoster(REGEX_EDITORIALS, RosterLoader::editorialFromLine);
	}
	
	private <T,R> List<T> loadInformationsFromRoster(String regex, Function<String, T> f) {
			return IOUtils.readContentFrom(rosterFile)
	                    .filter(Pattern.compile(regex).asPredicate())
	                    .map(f).collect(Collectors.toList());
    }

	private static Editorial editorialFromLine(String line) {
		return new Editorial(line.split(EDITORIAL_SEPARATOR)[1], Integer.valueOf(line.split(EDITORIAL_SEPARATOR)[0]));
	}

	private static Author authorFromLine(String line) {
		return new Author(line.split(AUTHOR_SEPARATOR)[0], line.split(AUTHOR_SEPARATOR)[1]);
	}
}
