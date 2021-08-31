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

    private static final String regexAuthors = "^([0-9][0-9]) ([a-z][a-z][a-z])";
    private static final String regexEditorials = "^([0-9][0-9]) ([a-z][a-z][a-z])";

	public List<Author> loadAuthorsFromRoster() {
		return this.loadInformationsFromRoster(regexAuthors, RosterLoader::authorFromLine);
	}

	public List<Editorial> loadEditorialsFromRoster() {
		return this.loadInformationsFromRoster(regexEditorials, RosterLoader::editorialFromLine);
	}
	
	private <T,R> List<T> loadInformationsFromRoster(String regex, Function<String, T> f) {
			return IOUtils.readContentFrom(rosterFile)
	                    .filter(Pattern.compile(regex).asPredicate())
	                    .map(f).collect(Collectors.toList());
    }

	private static Editorial editorialFromLine(String line) {
		return new Editorial(line.split(" ")[1], Integer.valueOf(line.split(" ")[0]));
	}

	private static Author authorFromLine(String line) {
		return new Author(line.split(" ")[1], line.split(" ")[0]);
	}
}
